package com.mallease.common.mybatis;

import com.mallease.common.util.LoginContextUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
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

        LocalDateTime now = LocalDateTime.now();

        fillAuditFields(parameter, sqlCommandType, LoginContextUtil.getOperatorNameOrSystem(), now);
        return invocation.proceed();
    }

    private void fillAuditFields(Object parameter, SqlCommandType sqlCommandType, String username, LocalDateTime now) {

        if (parameter == null) {
            return;
        }

        if (parameter instanceof Map<?, ?> map) {
            for (Object value : map.values()) {
                fillAuditFields(value, sqlCommandType, username, now);
            }
            return;
        }

        if (parameter instanceof Iterable<?> iterable) {
            for (Object item : iterable) {
                fillAuditFields(item, sqlCommandType, username, now);
            }
            return;
        }

        if (parameter.getClass().isArray()) {
            int length = Array.getLength(parameter);
            for (int i = 0; i < length; i++) {
                fillAuditFields(Array.get(parameter, i), sqlCommandType, username, now);
            }
            return;
        }

        if (parameter == null || isSimpleType(parameter.getClass())) {
            return;
        }

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
    }

    private boolean isSimpleType(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return true;
        }
        if (Number.class.isAssignableFrom(clazz)
                || CharSequence.class.isAssignableFrom(clazz)
                || Boolean.class.isAssignableFrom(clazz)
                || Character.class.isAssignableFrom(clazz)
                || java.util.Date.class.isAssignableFrom(clazz)
                || java.time.temporal.Temporal.class.isAssignableFrom(clazz)
                || java.util.UUID.class.isAssignableFrom(clazz)) {
            return true;
        }
        // 避免深入解析 MyBatis 自身的参数类型
        String pkg = clazz.getPackageName();
        return Objects.equals(pkg, "org.apache.ibatis.session") || pkg.startsWith("org.apache.ibatis");
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
