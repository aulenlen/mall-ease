package com.mallease.common.api;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.List;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-12 00:05
 **/
@Data
public class Page<T> {
    private Integer pageNum;
    private Integer pageSize;
    private Integer totalPage;
    private Long total;
    private List<T> list;

    /**
     * 将PageHelper分页后的list转为分页信息
     */
    public static <T> Page<T> restPage(List<T> list) {
        Page<T> result = new Page<T>();
        PageInfo<T> pageInfo = new PageInfo<T>(list);
        result.setTotalPage(pageInfo.getPages());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setTotal(pageInfo.getTotal());
        result.setList(pageInfo.getList());
        return result;
    }

    /**
     * 将PageHelper分页后的原始list与转换后的list合并为分页信息
     * <p>
     * 使用场景：当需要将Entity列表转换为VO列表时，分页信息在原始列表上，
     * 但实际返回的是转换后的列表。此方法从原始列表提取分页元数据，
     * 并将转换后的列表作为数据返回。
     *
     * @param originalList  PageHelper分页查询返回的原始列表（包含分页元数据）
     * @param convertedList 转换后的列表（如Entity转VO后的结果）
     * @param <S>           原始列表元素类型
     * @param <T>           转换后列表元素类型
     * @return 包含正确分页信息和转换后数据的分页对象
     */
    public static <S, T> Page<T> restPage(List<S> originalList, List<T> convertedList) {
        Page<T> result = new Page<T>();
        PageInfo<S> pageInfo = new PageInfo<S>(originalList);
        result.setTotalPage(pageInfo.getPages());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setTotal(pageInfo.getTotal());
        result.setList(convertedList);
        return result;
    }
}
