package com.mallease.user.controller;

import com.mallease.common.api.R;
import com.mallease.user.config.StpMemberUtil;
import com.mallease.user.model.client.cmd.AddressCmd;
import com.mallease.user.model.client.vo.AddressVO;
import com.mallease.user.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台收货地址管理控制器
 *
 * @author: Aulen
 * @create: 2026-03-11
 */
@Tag(name = "收货地址管理", description = "前台用户收货地址 CRUD")
@RestController
@RequestMapping("/user/address/portal")
@Slf4j
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "地址列表", description = "获取当前用户的所有收货地址，默认地址排在最前")
    @GetMapping("/list")
    public R<List<AddressVO>> list() {
        Long memberId = StpMemberUtil.getLoginIdAsLong();
        return R.success(addressService.list(memberId));
    }

    @Operation(summary = "地址详情", description = "根据地址ID获取详情")
    @GetMapping("/detail")
    public R<AddressVO> detail(@Parameter(description = "地址ID") @RequestParam Long id) {
        Long memberId = StpMemberUtil.getLoginIdAsLong();
        return R.success(addressService.getById(memberId, id));
    }

    @Operation(summary = "默认地址", description = "获取当前用户的默认收货地址")
    @GetMapping("/default")
    public R<AddressVO> getDefault() {
        Long memberId = StpMemberUtil.getLoginIdAsLong();
        return R.success(addressService.getDefault(memberId));
    }

    @Operation(summary = "新增地址", description = "新增收货地址，每人最多20条")
    @PostMapping("/create")
    public R<Long> create(@Validated(AddressCmd.Create.class) @RequestBody AddressCmd cmd) {
        Long memberId = StpMemberUtil.getLoginIdAsLong();
        return R.success(addressService.create(memberId, cmd));
    }

    @Operation(summary = "修改地址", description = "修改指定收货地址")
    @PutMapping("/update")
    public R<Void> update(@Validated(AddressCmd.Update.class) @RequestBody AddressCmd cmd) {
        Long memberId = StpMemberUtil.getLoginIdAsLong();
        addressService.update(memberId, cmd);
        return R.success(null);
    }

    @Operation(summary = "删除地址", description = "逻辑删除指定收货地址，若删除的是默认地址则自动切换")
    @DeleteMapping("/delete")
    public R<Void> delete(@Parameter(description = "地址ID") @RequestParam Long id) {
        Long memberId = StpMemberUtil.getLoginIdAsLong();
        addressService.delete(memberId, id);
        return R.success(null);
    }

    @Operation(summary = "设为默认", description = "将指定地址设为默认收货地址")
    @PutMapping("/setDefault")
    public R<Void> setDefault(@Parameter(description = "地址ID") @RequestParam Long id) {
        Long memberId = StpMemberUtil.getLoginIdAsLong();
        addressService.setDefault(memberId, id);
        return R.success(null);
    }
}
