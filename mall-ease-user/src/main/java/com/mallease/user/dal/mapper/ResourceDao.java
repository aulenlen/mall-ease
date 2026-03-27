package com.mallease.user.dal.mapper;

import com.mallease.user.dal.entity.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 后台资源表 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-11-07
 */
@Mapper
public interface ResourceDao {
    int deleteByPrimaryKey(Long id);

    int insert(Resource record);

    int insertSelective(Resource record);

    Resource selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Resource record);

    int updateByPrimaryKey(Resource record);

    List<Resource> selectByCategoryId(@Param("categoryId") Long categoryId);

    List<Resource> selectAll();

    List<Resource> selectByIds(@Param("ids") List<Long> ids);

    List<Resource> selectByCondition(@Param("name") String name,
                                     @Param("url") String url,
                                     @Param("categoryId") Long categoryId);
}