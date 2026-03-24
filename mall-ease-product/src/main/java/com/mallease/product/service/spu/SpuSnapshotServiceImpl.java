package com.mallease.product.service.spu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.common.exception.ApiException;
import com.mallease.product.controller.admin.spu.vo.SnapshotVO;
import com.mallease.product.convert.spu.SpuSnapshotConvert;
import com.mallease.product.dal.mapper.AttributeValueDao;
import com.mallease.product.dal.mapper.SkuDao;
import com.mallease.product.dal.mapper.SpuDao;
import com.mallease.product.dal.mapper.SpuDetailDao;
import com.mallease.product.dal.mapper.SpuSnapshotDao;
import com.mallease.product.dal.entity.AttributeValue;
import com.mallease.product.dal.entity.Sku;
import com.mallease.product.dal.entity.Spu;
import com.mallease.product.dal.entity.SpuDetail;
import com.mallease.product.dal.entity.SpuSnapshot;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

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
    public List<SpuSnapshot> buildSnapshots(List<Long> spuIds, LocalDateTime publishedAt) {

        List<Long> normalizedSpuIds = normalizeSpuIds(spuIds);

        BatchSnapshotSource source = loadSnapshotSource(normalizedSpuIds);

        return normalizedSpuIds.stream().map(spuId -> buildSnapshot(source, spuId, publishedAt)).toList();
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

    private SpuSnapshot buildSnapshot(BatchSnapshotSource source, Long spuId, LocalDateTime publishedAt) {

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

        int nextVersion = resolvePublishedVersion(spu);

        SnapshotVO snapshotVO = assembleSnapshotVO(snapshotSource, nextVersion, publishedAt);
        String snapshotJson = serializeSnapshotJson(spuId, snapshotVO);
        String snapshotHash = DigestUtils.md5DigestAsHex(snapshotJson.getBytes(StandardCharsets.UTF_8));

        return assembleSnapshotEntity(spu, nextVersion, publishedAt, snapshotJson, snapshotHash);
    }

    private BatchSnapshotSource loadSnapshotSource(List<Long> spuIds) {

        List<Spu> spus = spuDao.selectByIds(spuIds);
        Map<Long, Spu> spuMap = spus.stream().collect(Collectors.toMap(Spu::getId, Function.identity()));
        List<Long> missingSpuIds = spuIds.stream().filter(spuId -> !spuMap.containsKey(spuId)).toList();
        if (!missingSpuIds.isEmpty()) {
            throw new ApiException("商品不存在: " + missingSpuIds);
        }

        List<SpuDetail> spuDetails = spuDetailDao.selectBySpuIds(spuIds);
        List<Sku> skus = skuDao.selectBySpuIds(spuIds);
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

    private SnapshotVO assembleSnapshotVO(SnapshotSource source, Integer version, LocalDateTime publishedAt) {

        SnapshotVO snapshotVO = spuSnapshotConvert.toSnapshotVO(
                source.spu(),
                source.spuDetail(),
                source.params(),
                source.specs(),
                source.skus()
        );

        SnapshotVO.PublishMeta publishMeta = new SnapshotVO.PublishMeta();
        publishMeta.setVersion(version);
        publishMeta.setPublishedAt(publishedAt);
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

        List<Long> normalizedSpuIds = spuIds.stream()
                .distinct()
                .toList();

        return normalizedSpuIds;
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
