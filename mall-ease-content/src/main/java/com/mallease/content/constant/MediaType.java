package com.mallease.content.constant;

import cn.hutool.core.util.StrUtil;

import java.util.Locale;

/**
 * 媒体类型。
 */
public enum MediaType {

    /**
     * 图片。
     */
    IMAGE,

    /**
     * 视频。
     */
    VIDEO,

    /**
     * 其他。
     */
    OTHER;

    /**
     * 根据 MIME 类型和扩展名推断媒体类型。
     *
     * @param contentType MIME 类型
     * @param extension 扩展名
     * @return 媒体类型
     */
    public static MediaType infer(String contentType, String extension) {
        if (StrUtil.startWithIgnoreCase(contentType, "image/")) {
            return IMAGE;
        }
        if (StrUtil.startWithIgnoreCase(contentType, "video/")) {
            return VIDEO;
        }
        if (StrUtil.equalsAnyIgnoreCase(extension, "jpg", "jpeg", "png", "gif", "webp", "bmp", "svg", "ico")) {
            return IMAGE;
        }
        if (StrUtil.equalsAnyIgnoreCase(extension, "mp4", "mov", "avi", "mkv", "webm", "flv", "wmv", "m4v")) {
            return VIDEO;
        }
        return OTHER;
    }

    /**
     * OSS 路径中的类型目录。
     *
     * @return 小写类型名
     */
    public String pathName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
