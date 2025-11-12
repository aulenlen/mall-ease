package com.mallease.pms.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-12 14:33
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
public class BucketPolicyConfig {
    private String Version;
    private List<Statement> Statement;

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    public static class Statement {
        private String Effect;
        private String Principal;
        private String Action;
        private String Resource;

    }
}
