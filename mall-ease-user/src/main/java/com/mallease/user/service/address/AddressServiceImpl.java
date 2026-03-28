package com.mallease.user.service.address;

import com.mallease.common.exception.ApiException;
import com.mallease.user.convert.AddressConvert;
import com.mallease.user.dal.mapper.MemberAddressDao;
import com.mallease.user.controller.portal.address.vo.AddressReqVO;
import com.mallease.user.controller.portal.address.vo.AddressRespVO;
import com.mallease.user.dal.entity.MemberAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 收货地址服务实现
 *
 * @author: Aulen
 * @create: 2026-03-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private static final int MAX_ADDRESS_COUNT = 20;

    private final MemberAddressDao memberAddressDao;
    private final AddressConvert addressConvert;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(Long memberId, AddressReqVO reqVO) {

        int count = memberAddressDao.countByMemberId(memberId);
        if (count >= MAX_ADDRESS_COUNT) {
            throw new ApiException("收货地址最多只能添加" + MAX_ADDRESS_COUNT + "条");
        }

        MemberAddress address = addressConvert.toAddress(reqVO);
        address.setMemberId(memberId);

        if (count == 0) {
            address.setIsDefault(1);
        } else if (Integer.valueOf(1).equals(address.getIsDefault())) {
            memberAddressDao.clearDefaultByMemberId(memberId);
        } else {
            address.setIsDefault(0);
        }

        memberAddressDao.insert(address);
        return address.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long memberId, AddressReqVO reqVO) {
        MemberAddress existing = memberAddressDao.selectByIdAndMemberId(reqVO.getId(), memberId);
        if (existing == null) {
            throw new ApiException("地址不存在");
        }

        if (Integer.valueOf(1).equals(reqVO.getIsDefault()) && !Integer.valueOf(1).equals(existing.getIsDefault())) {
            memberAddressDao.clearDefaultByMemberId(memberId);
        }

        MemberAddress address = addressConvert.toAddress(reqVO);
        address.setId(reqVO.getId());
        memberAddressDao.updateByPrimaryKeySelective(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long memberId, Long id) {
        MemberAddress existing = memberAddressDao.selectByIdAndMemberId(id, memberId);
        if (existing == null) {
            throw new ApiException("地址不存在");
        }

        memberAddressDao.logicDeleteById(id, memberId);

        if (Integer.valueOf(1).equals(existing.getIsDefault())) {
            List<MemberAddress> remaining = memberAddressDao.selectByMemberId(memberId);
            if (!remaining.isEmpty()) {
                MemberAddress newest = remaining.get(0);
                newest.setIsDefault(1);
                memberAddressDao.updateByPrimaryKeySelective(
                        MemberAddress.builder().id(newest.getId()).isDefault(1).build()
                );
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long memberId, Long id) {
        MemberAddress existing = memberAddressDao.selectByIdAndMemberId(id, memberId);
        if (existing == null) {
            throw new ApiException("地址不存在");
        }

        memberAddressDao.clearDefaultByMemberId(memberId);

        memberAddressDao.updateByPrimaryKeySelective(
                MemberAddress.builder().id(id).isDefault(1).build()
        );
    }

    @Override
    public List<AddressRespVO> list(Long memberId) {
        List<MemberAddress> addresses = memberAddressDao.selectByMemberId(memberId);
        return addressConvert.toAddressRespList(addresses);
    }

    @Override
    public AddressRespVO getById(Long memberId, Long id) {
        MemberAddress address = memberAddressDao.selectByIdAndMemberId(id, memberId);
        if (address == null) {
            throw new ApiException("地址不存在");
        }
        return addressConvert.toAddressResp(address);
    }

    @Override
    public AddressRespVO getDefault(Long memberId) {
        MemberAddress address = memberAddressDao.selectDefaultByMemberId(memberId);
        return address == null ? null : addressConvert.toAddressResp(address);
    }
}
