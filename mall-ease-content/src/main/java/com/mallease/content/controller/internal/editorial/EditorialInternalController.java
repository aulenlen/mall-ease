package com.mallease.content.controller.internal.editorial;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.EditorialDetailDTO;
import com.mallease.common.dto.remote.EditorialDTO;
import com.mallease.content.convert.editorial.EditorialConvert;
import com.mallease.content.dal.entity.Editorial;
import com.mallease.content.dal.entity.EditorialSpuRelation;
import com.mallease.content.service.editorial.EditorialContentCodec;
import com.mallease.content.service.editorial.EditorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "编辑精选内部接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/content/editorial/internal")
public class EditorialInternalController {

    private final EditorialConvert editorialConvert;
    private final EditorialContentCodec editorialContentCodec;
    private final EditorialService editorialService;

    @Operation(summary = "获取已发布编辑精选")
    @GetMapping("/published")
    public R<List<EditorialDTO>> listPublished(
            @Parameter(description = "返回数量，默认10") @RequestParam(defaultValue = "10") Integer limit) {
        List<Editorial> editorialList = editorialService.listPublished(limit);
        if (editorialList.isEmpty()) {
            return R.success(Collections.emptyList());
        }
        List<EditorialDTO> dtoList = editorialConvert.toEditorialRemoteList(editorialList);
        List<Long> editorialIds = editorialList.stream().map(Editorial::getId).toList();
        Map<Long, List<Long>> spuIdsMap = editorialService.listSpuRelations(editorialIds).stream()
                .collect(Collectors.groupingBy(
                        EditorialSpuRelation::getEditorialId,
                        Collectors.mapping(EditorialSpuRelation::getSpuId, Collectors.toList())
                ));
        dtoList.forEach(dto -> dto.setSpuIds(spuIdsMap.getOrDefault(dto.getId(), Collections.emptyList())));
        return R.success(dtoList);
    }

    @Operation(summary = "获取已发布编辑精选详情")
    @GetMapping("/published/{id}")
    public R<EditorialDetailDTO> getPublishedDetail(@PathVariable Long id) {
        Editorial editorial = editorialService.getPublished(id);
        if (editorial == null) {
            return R.success(null);
        }
        EditorialDetailDTO detailDTO = editorialConvert.toEditorialDetailRemote(editorial);
        detailDTO.setContent(editorialContentCodec.decode(editorial.getContent()));
        detailDTO.setSpuIds(editorialService.listSpuIds(id));
        return R.success(detailDTO);
    }
}
