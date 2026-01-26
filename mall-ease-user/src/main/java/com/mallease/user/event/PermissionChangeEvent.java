package com.mallease.user.event;

import java.util.List;

/**
 * 权限变更事件
 * 用于在事务提交后（AFTER_COMMIT）踢下线受影响的管理员，强制重新登录以刷新 Sa-Token Session 中的权限列表
 */
public record PermissionChangeEvent(List<Long> adminIds) {}

