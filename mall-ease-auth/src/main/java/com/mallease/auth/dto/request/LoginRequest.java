package com.mallease.auth.dto.request;

import lombok.Data;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-09 21:45
 **/
@Data
public class LoginRequest {
    private String username;
    private String password;
    private String userType;
}
