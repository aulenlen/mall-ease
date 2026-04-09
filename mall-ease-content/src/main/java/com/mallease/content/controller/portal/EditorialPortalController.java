package com.mallease.content.controller.portal;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.EditorialDetailDTO;
import com.mallease.content.convert.editorial.EditorialConvert;
import com.mallease.content.dal.entity.Editorial;
import com.mallease.content.service.editorial.EditorialContentCodec;
import com.mallease.content.service.editorial.EditorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "编辑精选前台接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/portal/editorials")
public class EditorialPortalController {

    private final EditorialConvert editorialConvert;
    private final EditorialContentCodec editorialContentCodec;
    private final EditorialService editorialService;

    @Operation(summary = "获取已发布编辑精选详情")
    @GetMapping("/{id}")
    public R<EditorialDetailDTO> getDetail(@PathVariable Long id) {
        Editorial editorial = editorialService.getPublished(id);
        if (editorial == null) {
            return R.failed("编辑精选不存在");
        }
        EditorialDetailDTO detailDTO = editorialConvert.toEditorialDetailRemote(editorial);
        detailDTO.setContent(editorialContentCodec.decode(editorial.getContent()));
        detailDTO.setSpuIds(editorialService.listSpuIds(id));
        return R.success(detailDTO);
    }
}
