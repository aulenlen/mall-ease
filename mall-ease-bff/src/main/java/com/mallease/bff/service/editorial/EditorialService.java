package com.mallease.bff.service.editorial;

import com.mallease.bff.controller.portal.editorial.vo.EditorialDetailRespVO;

/** 编辑精选聚合服务。 */
public interface EditorialService {

    /** 获取编辑精选详情。 */
    EditorialDetailRespVO getDetail(Long id);
}
