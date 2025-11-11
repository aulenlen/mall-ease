package com.mallease.pms.service;

import com.mallease.pms.pojo.PmsBrand;

import java.util.List;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-10 18:43
 **/
public interface PmsBrandService {
    /**
     * 获取所有品牌
     * @return
     */
    List<PmsBrand> list();
}
