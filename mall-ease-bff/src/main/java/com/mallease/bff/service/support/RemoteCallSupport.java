package com.mallease.bff.service.support;

import com.mallease.common.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * 远程调用兜底支持类。
 *
 * @author: Aulen
 * @create: 2026-03-28
 */
@Component
@Slf4j
public class RemoteCallSupport {

    /**
     * 获取列表结果，失败时返回空列表。
     */
    public <T> List<T> getList(Supplier<R<List<T>>> supplier, String scene) {
        try {
            R<List<T>> result = supplier.get();
            if (result != null && result.getData() != null) {
                return result.getData();
            }
        } catch (Exception e) {
            log.error("获取{}失败", scene, e);
        }
        return Collections.emptyList();
    }

    /**
     * 获取单个结果，失败时返回 null。
     */
    public <T> T getOne(Supplier<R<T>> supplier, String scene) {
        try {
            R<T> result = supplier.get();
            if (result != null) {
                return result.getData();
            }
        } catch (Exception e) {
            log.error("获取{}失败", scene, e);
        }
        return null;
    }
}