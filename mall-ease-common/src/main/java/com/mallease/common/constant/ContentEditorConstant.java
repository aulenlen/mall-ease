package com.mallease.common.constant;

/**
 * 内容编辑器常量。
 *
 * @author: Codex
 * @create: 2026-04-18
 */
public final class ContentEditorConstant {

    /** 当前正文编辑器 schema 版本。 */
    public static final int EDITOR_SCHEMA_VERSION_V1 = 1;

    /** 正文内容说明。 */
    public static final String CONTENT_SCHEMA_DESCRIPTION = "正文内容（结构化 JSON，version + blocks 文档模型）";

    /** 编辑器结构版本说明。 */
    public static final String EDITOR_SCHEMA_VERSION_DESCRIPTION = "编辑器结构版本（兼容字段），当前固定为 1";

    /** 编辑器结构版本校验提示。 */
    public static final String EDITOR_SCHEMA_VERSION_INVALID_MESSAGE = "编辑器结构版本只能是 1";

    private ContentEditorConstant() {
    }
}
