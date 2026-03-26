package com.mallease.product.service.spu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.common.exception.ApiException;
import com.mallease.product.controller.admin.spu.vo.SnapshotVO;
import com.mallease.product.convert.spu.SpuSnapshotConvert;
import com.mallease.product.dal.entity.AttributeValue;
import com.mallease.product.dal.entity.Sku;
import com.mallease.product.dal.entity.Spu;
import com.mallease.product.dal.entity.SpuDetail;
import com.mallease.product.dal.entity.SpuSnapshot;
import com.mallease.product.dal.mapper.AttributeValueDao;
import com.mallease.product.dal.mapper.SkuDao;
import com.mallease.product.dal.mapper.SpuDao;
import com.mallease.product.dal.mapper.SpuDetailDao;
import com.mallease.product.dal.mapper.SpuSnapshotDao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class SpuSnapshotServiceImpl implements SpuSnapshotService {

    private final SpuSnapshotDao spuSnapshotDao;
    private final SpuDao spuDao;
    private final SpuDetailDao spuDetailDao;
    private final SkuDao skuDao;
    private final AttributeValueDao attributeValueDao;
    private final SpuSnapshotConvert spuSnapshotConvert;
    private final ObjectMapper objectMapper;

    @Override
    public List<PublishSnapshotPlan> buildPublishPlans(List<Long> spuIds, LocalDateTime publishedAt) {
        List<Long> normalizedSpuIds = normalizeSpuIds(spuIds);
        // 草稿对象
        BatchSnapshotSource source = loadSnapshotSource(normalizedSpuIds);
        // 已发布的快照
        Map<Long, SpuSnapshot> currentSnapshotMap = spuSnapshotDao.selectBySpuIds(normalizedSpuIds).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SpuSnapshot::getSpuId, Function.identity(), (left, right) -> right));

        return normalizedSpuIds.stream()
                .map(spuId -> buildPublishPlan(source, spuId, currentSnapshotMap, publishedAt))
                .toList();
    }

    @Override
    public int saveSnapshots(List<SpuSnapshot> snapshots) {
        if (snapshots == null || snapshots.isEmpty()) {
            return 0;
        }
        return spuSnapshotDao.upsertBySpuIds(snapshots);
    }

    @Override
    public SpuSnapshot getBySpuId(Long spuId) {
        return spuSnapshotDao.selectBySpuId(spuId);
    }

    private PublishSnapshotPlan buildPublishPlan(BatchSnapshotSource source, Long spuId,
                                                 Map<Long, SpuSnapshot> currentSnapshotMap, LocalDateTime publishedAt) {
        Spu spu = source.spuMap().get(spuId);
        List<Sku> skus = source.skuMap().getOrDefault(spuId, List.of());

        SnapshotSource snapshotSource = new SnapshotSource(
                spu,
                source.spuDetailMap().get(spuId),
                source.paramMap().getOrDefault(spuId, List.of()),
                source.specMap().getOrDefault(spuId, List.of()),
                skus
        );

        validatePublishable(snapshotSource);
        // 即将要发布对象
        SnapshotVO contentSnapshotVO = assembleContentSnapshotVO(snapshotSource);
        // 即将要发布对象的json
        String contentJson = serializeSnapshotJson(spuId, contentSnapshotVO);
        // 即将要发布对象的hash值
        String contentHash = calculateHash(contentJson);
        // 已发布对象的hash值
        String currentContentHash = resolveCurrentContentHash(spuId, currentSnapshotMap.get(spuId));
        // 对比是否有过更改
        boolean contentChanged = !Objects.equals(currentContentHash, contentHash);

        int nextVersion = resolvePublishedVersion(spu);
        SnapshotVO finalSnapshotVO = enrichPublishMeta(contentSnapshotVO, nextVersion, publishedAt, contentHash);
        String finalSnapshotJson = serializeSnapshotJson(spuId, finalSnapshotVO);

        SpuSnapshot snapshot = assembleSnapshotEntity(spu, nextVersion, publishedAt, finalSnapshotJson, contentHash);
        return new PublishSnapshotPlan(spuId, snapshot, contentChanged);
    }

    private BatchSnapshotSource loadSnapshotSource(List<Long> spuIds) {
        List<Spu> spus = spuDao.selectByIds(spuIds);
        Map<Long, Spu> spuMap = spus.stream().collect(Collectors.toMap(Spu::getId, Function.identity()));
        List<Long> missingSpuIds = spuIds.stream().filter(spuId -> !spuMap.containsKey(spuId)).toList();
        if (!missingSpuIds.isEmpty()) {
            throw new ApiException("商品不存在: " + missingSpuIds);
        }

        List<SpuDetail> spuDetails = spuDetailDao.selectBySpuIds(spuIds);
        List<Sku> skus = skuDao.selectEnabledBySpuIds(spuIds);
        List<AttributeValue> params = attributeValueDao.selectParamsBySpuIds(spuIds);
        List<AttributeValue> specs = skus.isEmpty() ? List.of() : attributeValueDao.selectSpecsBySkuIds(skus.stream().map(Sku::getId).toList());

        return new BatchSnapshotSource(
                spuMap,
                spuDetails.stream().collect(Collectors.toMap(SpuDetail::getSpuId, Function.identity(), (left, right) -> left)),
                params.stream().collect(Collectors.groupingBy(AttributeValue::getSpuId)),
                specs.stream().collect(Collectors.groupingBy(AttributeValue::getSpuId)),
                skus.stream().collect(Collectors.groupingBy(Sku::getSpuId))
        );
    }

    private void validatePublishable(SnapshotSource source) {
        if (source.spu() == null) {
            throw new ApiException("商品不存在");
        }
        if (source.spu().getDeleted() != null && source.spu().getDeleted() == 1) {
            throw new ApiException("商品已删除，不能上架");
        }
        if (source.skus() == null || source.skus().isEmpty()) {
            throw new ApiException("商品至少需要一个SKU才能上架");
        }

        boolean hasValidPrice = source.skus().stream()
                .allMatch(sku -> sku.getBasePrice() != null && sku.getBasePrice().signum() > 0);
        if (!hasValidPrice) {
            throw new ApiException("商品SKU销售价不能为空且必须大于0");
        }
    }

    private SnapshotVO assembleContentSnapshotVO(SnapshotSource source) {
        return spuSnapshotConvert.toSnapshotVO(
                source.spu(),
                source.spuDetail(),
                source.params(),
                source.specs(),
                source.skus()
        );
    }

    private SnapshotVO enrichPublishMeta(SnapshotVO snapshotVO, Integer version, LocalDateTime publishedAt, String snapshotHash) {
        SnapshotVO.PublishMeta publishMeta = new SnapshotVO.PublishMeta();
        publishMeta.setVersion(version);
        publishMeta.setPublishedAt(publishedAt);
        publishMeta.setSnapshotHash(snapshotHash);
        snapshotVO.setPublishMeta(publishMeta);
        return snapshotVO;
    }

    private String serializeSnapshotJson(Long spuId, SnapshotVO snapshotVO) {
        try {
            return objectMapper.writeValueAsString(snapshotVO);
        } catch (JsonProcessingException e) {
            log.error("商品快照序列化失败，spuId={}", spuId, e);
            throw new ApiException("商品快照生成失败");
        }
    }

    private String resolveCurrentContentHash(Long spuId, SpuSnapshot currentSnapshot) {
        if (currentSnapshot == null) {
            return null;
        }
        if (!StringUtils.hasText(currentSnapshot.getSnapshotJson())) {
            return currentSnapshot.getSnapshotHash();
        }
        try {
            SnapshotVO snapshotVO = objectMapper.readValue(currentSnapshot.getSnapshotJson(), SnapshotVO.class);
            snapshotVO.setPublishMeta(null);
            return calculateHash(objectMapper.writeValueAsString(snapshotVO));
        } catch (Exception e) {
            log.warn("解析当前快照内容摘要失败，退回使用已存摘要，spuId={}", spuId, e);
            return currentSnapshot.getSnapshotHash();
        }
    }

    private String calculateHash(String json) {
        return DigestUtils.md5DigestAsHex(json.getBytes(StandardCharsets.UTF_8));
    }

    private SpuSnapshot assembleSnapshotEntity(Spu spu, Integer version, LocalDateTime publishedAt, String snapshotJson, String snapshotHash) {
        SpuSnapshot snapshot = spuSnapshotConvert.toSnapshot(spu);
        snapshot.setVersion(version);
        snapshot.setPublishStatus(1);
        snapshot.setPublishedAt(publishedAt);
        snapshot.setSnapshotJson(snapshotJson);
        snapshot.setSnapshotHash(snapshotHash);
        return snapshot;
    }

    private int resolvePublishedVersion(Spu spu) {
        if (spu == null || spu.getVersion() == null || spu.getVersion() <= 0) {
            return 1;
        }
        return spu.getVersion();
    }

    private List<Long> normalizeSpuIds(List<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty()) {
            throw new ApiException("商品ID不能为空");
        }
        if (spuIds.stream().anyMatch(Objects::isNull)) {
            throw new ApiException("商品ID不能为空");
        }

        return spuIds.stream()
                .distinct()
                .toList();
    }

    private record BatchSnapshotSource(
            Map<Long, Spu> spuMap,
            Map<Long, SpuDetail> spuDetailMap,
            Map<Long, List<AttributeValue>> paramMap,
            Map<Long, List<AttributeValue>> specMap,
            Map<Long, List<Sku>> skuMap
    ) {
    }

    private record SnapshotSource(
            Spu spu,
            SpuDetail spuDetail,
            List<AttributeValue> params,
            List<AttributeValue> specs,
            List<Sku> skus
    ) {
    }
}
