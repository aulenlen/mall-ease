package com.mallease.auth.model.query;

import lombok.Data;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-09 21:45
 **/
@Data
public class LoginCmd {
    private String username;
    private String password;
}
