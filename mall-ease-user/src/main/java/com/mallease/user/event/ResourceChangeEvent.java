package com.mallease.user.event;

/**
 * 资源变更事件
 * 用于在事务提交后（AFTER_COMMIT）刷新 Redis 路径权限规则（auth:pathResourceMap）
 */
public record ResourceChangeEvent() {}
