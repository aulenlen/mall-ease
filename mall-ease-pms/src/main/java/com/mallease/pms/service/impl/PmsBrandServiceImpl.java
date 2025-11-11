package com.mallease.pms.service.impl;

import com.mallease.pms.dao.PmsBrandDao;
import com.mallease.pms.pojo.PmsBrand;
import com.mallease.pms.service.PmsBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-10 18:43
 **/
@Service
public class PmsBrandServiceImpl implements PmsBrandService {
    @Autowired
    private PmsBrandDao brandDao;
    @Override
    public List<PmsBrand> list() {
        return brandDao.selectAll();
    }
}
