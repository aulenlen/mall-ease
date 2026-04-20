package com.mallease.common.enums;

import java.util.Arrays;

/**
 * 跳转类型枚举
 *
 * @author: Aulen
 * @create: 2026-04-20
 */
public enum JumpType {
    NO_JUMP(0, "无跳转"),
    ACTIVITY(1, "活动页"),
    PRODUCT(2, "商品详情"),
    ARTICLE(3, "内容文章"),
    EXTERNAL(4, "外链");

    private final int code;
    private final String desc;

    JumpType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据跳转类型码获取枚举
     *
     * @param code 跳转类型码
     * @return 跳转类型枚举，无效值返回 null
     */
    public static JumpType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(type -> type.code == code)
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据跳转类型码获取描述
     *
     * @param code 跳转类型码
     * @return 跳转类型描述
     */
    public static String getDescriptionByCode(Integer code) {
        JumpType type = fromCode(code);
        return type != null ? type.desc : "未知类型";
    }

    /**
     * 判断是否需要跳转目标ID
     *
     * @return true-需要，false-不需要
     */
    public boolean needsTargetId() {
        return this == ACTIVITY || this == PRODUCT || this == ARTICLE;
    }

    /**
     * 判断是否需要外链URL
     *
     * @return true-需要，false-不需要
     */
    public boolean needsUrl() {
        return this == EXTERNAL;
    }
}
