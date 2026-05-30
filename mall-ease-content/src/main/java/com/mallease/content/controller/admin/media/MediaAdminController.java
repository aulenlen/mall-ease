package com.mallease.content.controller.admin.media;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.content.controller.admin.media.vo.MediaMoveReqVO;
import com.mallease.content.controller.admin.media.vo.MediaPageReqVO;
import com.mallease.content.controller.admin.media.vo.MediaRespVO;
import com.mallease.content.controller.admin.media.vo.MediaUploadRespVO;
import com.mallease.content.dal.entity.Media;
import com.mallease.content.service.media.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/content/media/files")
@Tag(name = "后台素材管理", description = "素材上传、查询、移动、下载和删除")
public class MediaAdminController {

    private final MediaService mediaService;

    @Operation(summary = "上传素材")
    @PostMapping
    public R<MediaUploadRespVO> upload(@Parameter(description = "文件") @RequestParam("file") MultipartFile file,
                                       @Parameter(description = "分组ID") @RequestParam(required = false) Long groupId) {
        try {
            return R.success(mediaService.upload(file, groupId));
        } catch (Exception ex) {
            log.error("上传素材失败", ex);
            return R.failed("上传素材失败: " + ex.getMessage());
        }
    }

    @Operation(summary = "分页查询素材")
    @GetMapping
    public R<Page<MediaRespVO>> page(@Validated @ModelAttribute MediaPageReqVO reqVO) {
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<Media> mediaList = mediaService.page(reqVO);
        return R.success(PageUtils.buildPage(mediaList, mediaList.stream().map(this::toRespVO).toList()));
    }

    @Operation(summary = "查询素材详情")
    @GetMapping("/{id}")
    public R<MediaRespVO> get(@PathVariable Long id) {
        Media media = mediaService.get(id);
        if (media == null) {
            return R.failed("素材不存在");
        }
        return R.success(toRespVO(media));
    }

    @Operation(summary = "移动素材分组")
    @PutMapping("/{id}/group")
    public R<Integer> move(@PathVariable Long id, @Validated @RequestBody MediaMoveReqVO reqVO) {
        try {
            int count = mediaService.move(id, reqVO.getGroupId());
            return count > 0 ? R.success(count) : R.failed("素材不存在");
        } catch (IllegalArgumentException e) {
            return R.failed(ResultCode.VALIDATE_FAILED, e.getMessage());
        }
    }

    @Operation(summary = "删除素材")
    @DeleteMapping("/{id}")
    public R<Integer> delete(@PathVariable Long id) {
        int count = mediaService.delete(id);
        return count > 0 ? R.success(count) : R.failed("素材不存在");
    }

    @Operation(summary = "下载素材")
    @GetMapping("/{id}/download")
    public void download(@PathVariable Long id, HttpServletResponse response) {
        mediaService.download(id, response);
    }

    private MediaRespVO toRespVO(Media media) {
        return MediaRespVO.builder()
                .id(media.getId())
                .groupId(media.getGroupId())
                .hash(media.getHash())
                .originalName(media.getOriginalName())
                .url(media.getUrl())
                .thumbnailUrl(media.getThumbnailUrl())
                .mediaType(media.getMediaType())
                .fileSize(media.getFileSize())
                .extension(media.getExtension())
                .creator(media.getCreator())
                .updater(media.getUpdater())
                .createTime(media.getCreateTime())
                .updateTime(media.getUpdateTime())
                .build();
    }
}
