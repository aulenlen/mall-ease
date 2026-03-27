package com.mallease.user.service.address;

import com.mallease.user.controller.portal.address.vo.AddressReqVO;
import com.mallease.user.controller.portal.address.vo.AddressRespVO;

import java.util.List;

/**
 * 收货地址服务接口
 *
 * @author: Aulen
 * @create: 2026-03-11
 */
public interface AddressService {

    /**
     * 新增收货地址
     *
     * @param memberId 会员ID
     * @param reqVO     地址请求
     * @return 新地址ID
     */
    Long create(Long memberId, AddressReqVO reqVO);

    /**
     * 修改收货地址
     *
     * @param memberId 会员ID
     * @param reqVO     地址请求
     */
    void update(Long memberId, AddressReqVO reqVO);

    /**
     * 删除收货地址（逻辑删除）
     *
     * @param memberId 会员ID
     * @param id       地址ID
     */
    void delete(Long memberId, Long id);

    /**
     * 设为默认地址
     *
     * @param memberId 会员ID
     * @param id       地址ID
     */
    void setDefault(Long memberId, Long id);

    /**
     * 查询当前用户的地址列表（默认地址排在最前）
     *
     * @param memberId 会员ID
     * @return 地址列表
     */
    List<AddressRespVO> list(Long memberId);

    /**
     * 查询地址详情
     *
     * @param memberId 会员ID
     * @param id       地址ID
     * @return 地址详情
     */
    AddressRespVO getById(Long memberId, Long id);

    /**
     * 查询默认地址
     *
     * @param memberId 会员ID
     * @return 默认地址，不存在返回 null
     */
    AddressRespVO getDefault(Long memberId);
}
