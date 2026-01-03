package com.mallease.product.feign;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.SpuIndexDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "mall-ease-search")
public interface SpuSearchFeignClient {
    @PostMapping("/search/index/batch")
    R<?> indexBatch(@RequestBody List<SpuIndexDTO> spuIndexDTOList);
}
