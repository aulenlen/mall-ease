package com.mallease.content.service.media;

import com.mallease.content.controller.admin.media.vo.MediaPageReqVO;
import com.mallease.content.controller.admin.media.vo.MediaUploadRespVO;
import com.mallease.content.dal.entity.Media;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 素材服务接口。
 */
public interface MediaService {

    /**
     * 上传素材。
     *
     * @param file 文件
     * @param groupId 分组ID；null 或 0 表示未分组
     * @return 上传结果
     */
    MediaUploadRespVO upload(MultipartFile file, Long groupId);

    /**
     * 查询素材详情。
     *
     * @param id 素材ID
     * @return 素材实体
     */
    Media get(Long id);

    /**
     * 分页查询素材。
     *
     * @param reqVO 查询参数
     * @return 素材列表
     */
    List<Media> page(MediaPageReqVO reqVO);

    /**
     * 移动素材到指定分组。
     *
     * @param id 素材ID
     * @param groupId 分组ID；null 或 0 表示未分组
     * @return 影响行数
     */
    int move(Long id, Long groupId);

    /**
     * 逻辑删除素材。
     *
     * @param id 素材ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 下载素材。
     *
     * @param id 素材ID
     * @param response HTTP 响应
     */
    void download(Long id, HttpServletResponse response);
}