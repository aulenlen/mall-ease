package com.mallease.product.constant;

/**
 * 商品模块通用状态常量。
 */
public final class ProductStatusConstants {

    public static final int FLAG_DISABLED = 0;
    public static final int FLAG_ENABLED = 1;

    public static final int ATTR_TYPE_PARAM = 0;
    public static final int ATTR_TYPE_SPEC = 1;

    public static final int STOCK_STATUS_OUT_OF_STOCK = 0;
    public static final int STOCK_STATUS_IN_STOCK = 1;

    public static final int DEFAULT_SORT = 0;
    public static final int INITIAL_SALE = 0;
    public static final int INITIAL_VERSION = 1;
    public static final int STAGED_CHANGES_YES = 1;
    public static final int PUBLISHED_VERSION_INITIAL = 0;
    public static final int DELETED_NO = 0;

    public static final int PUBLISH_STATUS_DRAFT = 0;
    public static final int PUBLISH_STATUS_PUBLISHED = 1;
    public static final int VERIFY_STATUS_PENDING = 0;
    public static final int NEW_STATUS_NO = 0;
    public static final int RECOMMEND_STATUS_NO = 0;

    private ProductStatusConstants() {
    }
}
