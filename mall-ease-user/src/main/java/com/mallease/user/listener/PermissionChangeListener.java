package com.mallease.user.listener;

import cn.dev33.satoken.stp.StpLogic;
import com.mallease.common.constant.AuthConstant;
import com.mallease.user.event.PermissionChangeEvent;
import com.mallease.user.event.ResourceChangeEvent;
import com.mallease.user.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Objects;

/**
 * 权限变更监听器
 *
 * @author: Aulen
 * @create: 2026-01-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionChangeListener {

    /**
     * 管理员账号体系的 StpLogic（Sa-Token 多账号体系）
     */
    private static final StpLogic STP_ADMIN_LOGIC = new StpLogic(AuthConstant.LOGIN_TYPE_ADMIN);

    private final ResourceService resourceService;

    /**
     * 资源变更：刷新 Redis 路径权限规则
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onResourceChanged(ResourceChangeEvent event) {
        try {
            resourceService.initResource();
            log.debug("刷新 Redis 路径权限规则成功");
        } catch (Exception e) {
            log.warn("刷新 Redis 路径权限规则失败: {}", e.getMessage());
        }
    }

    /**
     * 权限变更：踢下线受影响的管理员
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPermissionChanged(PermissionChangeEvent event) {
        if (event == null) {
            return;
        }

        List<Long> adminIds = event.adminIds();
        if (adminIds == null || adminIds.isEmpty()) {
            return;
        }

        adminIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .forEach(adminId -> {
                    try {
                        STP_ADMIN_LOGIC.kickout(adminId);
                        log.debug("踢下线成功: adminId={}", adminId);
                    } catch (Exception e) {
                        log.warn("踢下线失败: adminId={}, err={}", adminId, e.getMessage());
                    }
                });
    }
}