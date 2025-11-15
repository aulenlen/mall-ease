package com.mallease.common.converter;

import com.mallease.common.api.Page;
import java.util.List;

/**
 * 基础转换器接口
 *
 * @author: Aulen
 * @create: 2025-11-15
 * @param <E> Entity实体类型
 * @param <D> DTO类型
 * @param <V> VO类型
 */
public interface BaseConverter<E, D, V> {

    /**
     * Entity → DTO
     */
    D entityToDto(E entity);

    /**
     * DTO → Entity
     */
    E dtoToEntity(D dto);

    /**
     * DTO → VO
     */
    V dtoToVo(D dto);

    /**
     * Entity列表 → DTO列表
     */
    List<D> entityListToDtoList(List<E> entities);

    /**
     * DTO列表 → VO列表
     */
    List<V> dtoListToVoList(List<D> dtos);

    /**
     * DTO分页 → VO分页
     */
    default Page<V> dtoPageToVoPage(Page<D> dtoPage) {
        Page<V> voPage = new Page<>();
        voPage.setPageNum(dtoPage.getPageNum());
        voPage.setPageSize(dtoPage.getPageSize());
        voPage.setTotalPage(dtoPage.getTotalPage());
        voPage.setTotal(dtoPage.getTotal());
        voPage.setList(dtoListToVoList(dtoPage.getList()));
        return voPage;
    }
}
