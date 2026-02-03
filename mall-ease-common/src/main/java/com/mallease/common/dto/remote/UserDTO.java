package com.mallease.common.dto.remote;

import lombok.*;

import java.util.List;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-07
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String clientId;
    private List<String> permissionList;
}
