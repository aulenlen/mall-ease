package com.mallease.content.constant;

import com.mallease.common.constant.SlotCodeConstants;
import com.mallease.common.dto.content.SlotRenderType;

import java.util.Arrays;

/**
 * 系统内置槽位模板。
 */
public enum SlotTemplate {

    HOME_TOP_SWIPER(SlotCodeConstants.HOME_TOP_SWIPER, "首页顶部轮播", "home", SlotRenderType.SWIPER.getCode()),
    HOME_ARTICLE_FEED(SlotCodeConstants.HOME_ARTICLE_FEED, "首页文章区", "home", SlotRenderType.ARTICLE_LIST.getCode());

    private final String code;
    private final String name;
    private final String pageCode;
    private final String renderType;

    SlotTemplate(String code, String name, String pageCode, String renderType) {
        this.code = code;
        this.name = name;
        this.pageCode = pageCode;
        this.renderType = renderType;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getPageCode() {
        return pageCode;
    }

    public String getRenderType() {
        return renderType;
    }

    public static SlotTemplate fromCode(String code) {
        return Arrays.stream(values())
                .filter(template -> template.code.equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }
}
