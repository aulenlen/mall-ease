package com.mallease.user.dao;

import com.mallease.user.model.data.MemberAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员收货地址 Mapper 接口
 *
 * @author: Aulen
 * @create: 2026-03-11
 */
@Mapper
public interface MemberAddressDao {

    /**
     * 插入记录
     *
     * @param record 地址记录
     * @return 影响行数
     */
    int insert(MemberAddress record);

    /**
     * 根据主键选择性更新
     *
     * @param record 地址记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(MemberAddress record);

    /**
     * 逻辑删除（按 ID + 会员ID）
     *
     * @param id       地址ID
     * @param memberId 会员ID
     * @return 影响行数
     */
    int logicDeleteById(@Param("id") Long id, @Param("memberId") Long memberId);

    /**
     * 根据 ID 和会员ID 查询（含归属校验）
     *
     * @param id       地址ID
     * @param memberId 会员ID
     * @return 地址记录
     */
    MemberAddress selectByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId);

    /**
     * 查询会员的所有地址（默认地址排在最前，按创建时间倒序）
     *
     * @param memberId 会员ID
     * @return 地址列表
     */
    List<MemberAddress> selectByMemberId(@Param("memberId") Long memberId);

    /**
     * 查询会员的默认地址
     *
     * @param memberId 会员ID
     * @return 默认地址
     */
    MemberAddress selectDefaultByMemberId(@Param("memberId") Long memberId);

    /**
     * 统计会员地址数量
     *
     * @param memberId 会员ID
     * @return 地址数量
     */
    int countByMemberId(@Param("memberId") Long memberId);

    /**
     * 清除会员的所有默认地址标记
     *
     * @param memberId 会员ID
     * @return 影响行数
     */
    int clearDefaultByMemberId(@Param("memberId") Long memberId);
}
