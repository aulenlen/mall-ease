package com.mallease.common.enums;

/**
 * 用户类型枚举
 */
public enum UserType {
    ADMIN("admin-app", "管理员"),
    MEMBER("portal-app", "普通用户");

    private final String clientId;
    private final String description;

    UserType(String clientId, String description) {
        this.clientId = clientId;
        this.description = description;
    }

    public String getClientId() {
        return clientId;
    }

    public String getDescription() {
        return description;
    }
}
