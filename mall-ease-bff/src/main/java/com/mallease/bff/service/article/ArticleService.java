package com.mallease.bff.service.article;

import com.mallease.bff.controller.portal.article.vo.ArticleDetailRespVO;

public interface ArticleService {

    ArticleDetailRespVO getDetail(Long id);
}
