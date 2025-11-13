package com.mallease.cms.pojo;

import lombok.Data;

/**
 * 优选专区表
 *
 * @author: Claude
 * @create: 2025-11-13
 */
@Data
public class CmsPrefrenceArea {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 副标题
     */
    private String subTitle;

    /**
     * 展示图片
     */
    private byte[] pic;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 显示状态：0->不显示；1->显示
     */
    private Integer showStatus;
}
