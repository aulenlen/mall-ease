package com.mallease.user.controller.admin.user.vo;

import com.mallease.user.controller.admin.menu.vo.MenuRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 当前登录管理员信息返回
 *
 * @author: Aulen
 * @create: 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "当前登录管理员信息返回")
public class CurrentAdminInfoRespVO {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "头像")
    private String icon;

    @Schema(description = "角色标识列表")
    private List<String> roles;

    @Schema(description = "菜单列表")
    private List<MenuRespVO> menus;
}