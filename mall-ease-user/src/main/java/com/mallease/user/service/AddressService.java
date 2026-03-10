package com.mallease.user.service;

import com.mallease.user.model.client.cmd.AddressCmd;
import com.mallease.user.model.client.vo.AddressVO;

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
     * @param cmd      地址命令
     * @return 新地址ID
     */
    Long create(Long memberId, AddressCmd cmd);

    /**
     * 修改收货地址
     *
     * @param memberId 会员ID
     * @param cmd      地址命令
     */
    void update(Long memberId, AddressCmd cmd);

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
    List<AddressVO> list(Long memberId);

    /**
     * 查询地址详情
     *
     * @param memberId 会员ID
     * @param id       地址ID
     * @return 地址详情
     */
    AddressVO getById(Long memberId, Long id);

    /**
     * 查询默认地址
     *
     * @param memberId 会员ID
     * @return 默认地址，不存在返回 null
     */
    AddressVO getDefault(Long memberId);
}
