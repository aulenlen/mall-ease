package com.mallease.bff.controller.portal.editorial;

import com.mallease.bff.controller.portal.editorial.vo.EditorialDetailRespVO;
import com.mallease.bff.service.editorial.EditorialService;
import com.mallease.common.api.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** BFF 编辑精选接口。 */
@Tag(name = "BFF编辑精选", description = "编辑精选详情聚合接口")
@RestController
@RequestMapping("/portal/editorials")
@RequiredArgsConstructor
public class EditorialPortalController {

    private final EditorialService editorialService;

    @Operation(summary = "获取编辑精选详情", description = "聚合返回文章详情和文中商品")
    @GetMapping("/{id}")
    public R<EditorialDetailRespVO> getDetail(@PathVariable Long id) {
        EditorialDetailRespVO detailRespVO = editorialService.getDetail(id);
        if (detailRespVO == null) {
            return R.failed("编辑精选不存在或暂不可用");
        }
        return R.success(detailRespVO);
    }
}
