package com.mallease.admin.dto.request;

import lombok.Builder;
import lombok.Data;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-10 00:41
 **/
@Data
@Builder
public class LoginRequestDto {
    private String username;
    private String password;
    private String userType;
}
