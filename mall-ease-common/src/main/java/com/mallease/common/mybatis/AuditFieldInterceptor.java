package com.mallease.common.mybatis;

import com.mallease.common.util.LoginContextUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Properties;

/**
 * MyBatis 审计字段自动填充拦截器
 *
 * @author: Aulen
 * @create: 2025-01-01
 */
@Intercepts(
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
)
public class AuditFieldInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        if (parameter == null) {
            return invocation.proceed();
        }
        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        String username = LoginContextUtil.getUserName();
        LocalDateTime now = LocalDateTime.now();

        if (SqlCommandType.INSERT == sqlCommandType) {
            // 插入时填充 creator, createTime, updater, updateTime
            setFieldValue(parameter, "creator", username);
            setFieldValue(parameter, "createTime", now);
            setFieldValue(parameter, "updater", username);
            setFieldValue(parameter, "updateTime", now);
            setFieldValue(parameter, "deleted", 0);
        } else if (SqlCommandType.UPDATE == sqlCommandType) {
            // 更新时只填充 updater, updateTime
            setFieldValue(parameter, "updater", username);
            setFieldValue(parameter, "updateTime", now);
        }
        return invocation.proceed();
    }

    private void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            Field field = getField(obj.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                // 只在字段为空时填充（避免覆盖手动设置的值）
                if (field.get(obj) == null) {
                    field.set(obj, value);
                }
            }
        } catch (Exception ignored) {
        }

    }

    private Field getField(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
