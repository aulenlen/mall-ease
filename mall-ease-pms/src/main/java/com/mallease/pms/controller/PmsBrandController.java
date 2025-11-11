package com.mallease.pms.controller;

import com.mallease.common.api.R;
import com.mallease.pms.pojo.PmsBrand;
import com.mallease.pms.service.PmsBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-10 18:42
 **/
@RestController
@RequestMapping("/brand")
public class PmsBrandController {
    @Autowired
    private PmsBrandService brandService;

    /**
     * 获取全部品牌列表
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<PmsBrand>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        List<PmsBrand> brandList = brandService.list();
        return R.success(brandList);
    }
}
