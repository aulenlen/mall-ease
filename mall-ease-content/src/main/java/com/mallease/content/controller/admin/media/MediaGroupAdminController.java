package com.mallease.content.controller.admin.media;

import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.content.controller.admin.media.vo.MediaGroupReqVO;
import com.mallease.content.controller.admin.media.vo.MediaGroupRespVO;
import com.mallease.content.dal.entity.MediaGroup;
import com.mallease.content.service.media.MediaGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "后台素材分组管理", description = "素材分组增删改查")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/content/media/groups")
public class MediaGroupAdminController {

    private final MediaGroupService mediaGroupService;

    @Operation(summary = "创建素材分组")
    @PostMapping
    public R<Long> create(@Validated(MediaGroupReqVO.Create.class) @RequestBody MediaGroupReqVO reqVO) {
        try {
            return R.success(mediaGroupService.create(toMediaGroup(reqVO)));
        } catch (IllegalArgumentException e) {
            return R.failed(ResultCode.VALIDATE_FAILED, e.getMessage());
        }
    }

    @Operation(summary = "更新素材分组")
    @PutMapping("/{id}")
    public R<Integer> update(@PathVariable Long id,
                             @Validated(MediaGroupReqVO.Update.class) @RequestBody MediaGroupReqVO reqVO) {
        try {
            MediaGroup mediaGroup = toMediaGroup(reqVO);
            mediaGroup.setId(id);
            int count = mediaGroupService.update(mediaGroup);
            return count > 0 ? R.success(count) : R.failed("分组不存在");
        } catch (IllegalArgumentException e) {
            return R.failed(ResultCode.VALIDATE_FAILED, e.getMessage());
        }
    }

    @Operation(summary = "删除素材分组")
    @DeleteMapping("/{id}")
    public R<Integer> delete(@PathVariable Long id) {
        int count = mediaGroupService.delete(id);
        return count > 0 ? R.success(count) : R.failed("分组不存在");
    }

    @Operation(summary = "查询素材分组详情")
    @GetMapping("/{id}")
    public R<MediaGroupRespVO> get(@PathVariable Long id) {
        MediaGroup mediaGroup = mediaGroupService.get(id);
        if (mediaGroup == null) {
            return R.failed("分组不存在");
        }
        return R.success(toRespVO(mediaGroup));
    }

    @Operation(summary = "查询素材分组列表")
    @GetMapping
    public R<List<MediaGroupRespVO>> list() {
        return R.success(mediaGroupService.list().stream()
                .map(this::toRespVO)
                .toList());
    }

    private MediaGroup toMediaGroup(MediaGroupReqVO reqVO) {
        MediaGroup mediaGroup = new MediaGroup();
        mediaGroup.setName(reqVO.getName());
        mediaGroup.setSort(reqVO.getSort());
        return mediaGroup;
    }

    private MediaGroupRespVO toRespVO(MediaGroup mediaGroup) {
        return MediaGroupRespVO.builder()
                .id(mediaGroup.getId())
                .name(mediaGroup.getName())
                .sort(mediaGroup.getSort())
                .createTime(mediaGroup.getCreateTime())
                .updateTime(mediaGroup.getUpdateTime())
                .build();
    }
}
