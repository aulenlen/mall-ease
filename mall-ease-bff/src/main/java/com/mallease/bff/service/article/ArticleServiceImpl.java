package com.mallease.bff.service.article;

import com.mallease.bff.controller.portal.article.vo.ArticleDetailRespVO;
import com.mallease.bff.convert.ArticleConvert;
import com.mallease.bff.feign.content.ContentFeignClient;
import com.mallease.bff.feign.product.ProductFeignClient;
import com.mallease.bff.service.support.RemoteCallSupport;
import com.mallease.common.dto.remote.ArticleDetailDTO;
import com.mallease.common.dto.remote.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/** 文章聚合服务实现。 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ContentFeignClient contentFeignClient;
    private final ProductFeignClient productFeignClient;
    private final ArticleConvert articleConvert;
    private final RemoteCallSupport remoteCallSupport;

    @Override
    public ArticleDetailRespVO getDetail(Long id) {
        ArticleDetailDTO articleDetailDTO = remoteCallSupport.getOne(
                () -> contentFeignClient.getPublishedArticleDetail(id),
                "文章详情"
        );
        if (articleDetailDTO == null) {
            return null;
        }

        List<Long> spuIds = articleDetailDTO.getSpuIds();
        List<ProductDTO> productList = CollectionUtils.isEmpty(spuIds)
                ? Collections.emptyList()
                : remoteCallSupport.getList(
                        () -> productFeignClient.detailSnapshots(spuIds),
                        "文中商品"
                );
        return articleConvert.toArticleDetailResp(articleDetailDTO, productList);
    }
}
