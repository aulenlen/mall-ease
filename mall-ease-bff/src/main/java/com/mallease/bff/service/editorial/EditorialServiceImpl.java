package com.mallease.bff.service.editorial;

import com.mallease.bff.controller.portal.editorial.vo.EditorialDetailRespVO;
import com.mallease.bff.convert.EditorialConvert;
import com.mallease.bff.feign.content.ContentFeignClient;
import com.mallease.bff.feign.product.ProductFeignClient;
import com.mallease.bff.service.support.RemoteCallSupport;
import com.mallease.common.dto.remote.EditorialDetailDTO;
import com.mallease.common.dto.remote.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/** 编辑精选聚合服务实现。 */
@Service
@RequiredArgsConstructor
public class EditorialServiceImpl implements EditorialService {

    private final ContentFeignClient contentFeignClient;
    private final ProductFeignClient productFeignClient;
    private final EditorialConvert editorialConvert;
    private final RemoteCallSupport remoteCallSupport;

    @Override
    public EditorialDetailRespVO getDetail(Long id) {
        EditorialDetailDTO editorialDetailDTO = remoteCallSupport.getOne(
                () -> contentFeignClient.getPublishedEditorialDetail(id),
                "文章详情"
        );
        if (editorialDetailDTO == null) {
            return null;
        }

        List<Long> spuIds = editorialDetailDTO.getSpuIds();
        List<ProductDTO> productList = CollectionUtils.isEmpty(spuIds)
                ? Collections.emptyList()
                : remoteCallSupport.getList(
                        () -> productFeignClient.detailSnapshots(spuIds),
                        "文中商品"
                );
        return editorialConvert.toEditorialDetailResp(editorialDetailDTO, productList);
    }
}
