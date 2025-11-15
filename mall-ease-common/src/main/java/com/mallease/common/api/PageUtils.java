package com.mallease.common.api;

import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.function.Function;

/**
 * 分页工具类
 * 解决PageHelper分页后进行对象转换导致分页信息丢失的问题
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
public class PageUtils {

    /**
     * 转换分页结果
     * 从Entity列表转换为VO列表，并保留分页信息
     *
     * @param entityList 实体列表（PageHelper查询结果）
     * @param converter  转换函数（Entity -> VO）
     * @param <E>        实体类型
     * @param <V>        VO类型
     * @return 包含VO列表和完整分页信息的Page对象
     */
    public static <E, V> Page<V> convertPage(List<E> entityList, Function<List<E>, List<V>> converter) {
        // 1. 从原始查询结果获取分页信息
        PageInfo<E> pageInfo = new PageInfo<>(entityList);

        // 2. 转换为VO列表
        List<V> voList = converter.apply(pageInfo.getList());

        // 3. 构建Page对象，保留分页信息
        Page<V> result = new Page<>();
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setTotal(pageInfo.getTotal());
        result.setTotalPage(pageInfo.getPages());
        result.setList(voList);

        return result;
    }

    /**
     * 简化版：直接传入PageHelper查询结果和转换后的VO列表
     * 适用于已经完成转换的场景
     *
     * @param entityList 实体列表（用于获取分页信息）
     * @param voList     已转换的VO列表
     * @param <E>        实体类型
     * @param <V>        VO类型
     * @return 包含VO列表和完整分页信息的Page对象
     */
    public static <E, V> Page<V> buildPage(List<E> entityList, List<V> voList) {
        PageInfo<E> pageInfo = new PageInfo<>(entityList);

        Page<V> result = new Page<>();
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setTotal(pageInfo.getTotal());
        result.setTotalPage(pageInfo.getPages());
        result.setList(voList);

        return result;
    }
}
