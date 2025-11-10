package com.mallease.admin.feign;

import com.mallease.common.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-10 15:02
 **/
@FeignClient(name = "mall-ease-ums")
public interface UmsServiceFeignClient {
    @GetMapping("/ums/user/current")
    R<Map<String, Object>> getCurrentAdmin();
}
