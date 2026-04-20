package com.mallease.content.constant;

/**
 * 内容模块状态常量。
 *
 * @author: Codex
 * @create: 2026-04-18
 */
public final class ContentStatusConstants {

    /** 文章状态说明。 */
    public static final String ARTICLE_STATUS_SCHEMA = "状态：0-草稿 1-已发布 2-已下架";

    /** 文章状态校验提示。 */
    public static final String ARTICLE_STATUS_INVALID_MESSAGE = "状态值只能是 0、1 或 2";

    /** 文章草稿状态。 */
    public static final int ARTICLE_STATUS_DRAFT = 0;

    /** 文章已发布状态。 */
    public static final int ARTICLE_STATUS_PUBLISHED = 1;

    /** 文章已下架状态。 */
    public static final int ARTICLE_STATUS_OFFLINE = 2;

    /** 启用状态说明。 */
    public static final String ENABLE_STATUS_SCHEMA = "状态：0-禁用 1-启用";

    /** 启用状态校验提示。 */
    public static final String ENABLE_STATUS_INVALID_MESSAGE = "状态值只能是 0 或 1";

    /** 禁用状态。 */
    public static final int ENABLE_STATUS_DISABLED = 0;

    /** 启用状态。 */
    public static final int ENABLE_STATUS_ENABLED = 1;

    private ContentStatusConstants() {
    }

    /**
     * 校验文章状态是否合法。
     *
     * @param status 状态值
     * @return true-合法，false-非法
     */
    public static boolean isValidArticleStatus(Integer status) {
        return status != null && status >= ARTICLE_STATUS_DRAFT && status <= ARTICLE_STATUS_OFFLINE;
    }

    /**
     * 校验启用状态是否合法。
     *
     * @param status 状态值
     * @return true-合法，false-非法
     */
    public static boolean isValidEnableStatus(Integer status) {
        return status != null && (status == ENABLE_STATUS_DISABLED || status == ENABLE_STATUS_ENABLED);
    }
}
