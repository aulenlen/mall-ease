package com.mallease.content.service.media;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.mallease.common.exception.ApiException;
import com.mallease.content.config.StorageProperties;
import com.mallease.content.constant.MediaType;
import com.mallease.content.controller.admin.media.vo.MediaPageReqVO;
import com.mallease.content.controller.admin.media.vo.MediaUploadRespVO;
import com.mallease.content.dal.entity.Media;
import com.mallease.content.dal.entity.MediaGroup;
import com.mallease.content.dal.mapper.MediaDao;
import com.mallease.content.dal.mapper.MediaGroupDao;
import com.mallease.content.service.storage.ObjectStorageService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

/**
 * 素材服务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private static final int THUMBNAIL_MAX_SIZE = 240;
    private static final String THUMBNAIL_CONTENT_TYPE = "image/jpeg";
    private static final String THUMBNAIL_EXTENSION = "jpg";

    private final MediaDao mediaDao;
    private final MediaGroupDao mediaGroupDao;
    private final StorageProperties storageProperties;
    private final ObjectStorageService objectStorageService;
@Override
    public MediaUploadRespVO upload(MultipartFile file, Long groupId) {
        try {
            validateUploadFile(file);
            byte[] bytes = file.getBytes();
            String contentType = file.getContentType();
            Media media = buildMedia(file, groupId, bytes, contentType);

            Media existing = getReusableMedia(media);
            if (existing != null) {
                return buildUploadResp(existing, true);
            }

            uploadMediaFiles(media, bytes, contentType);
            return saveMedia(media);
        } catch (ApiException ex) {
            throw ex;
        } catch (IOException e) {
            throw new ApiException("读取文件失败");
        } catch (Exception ex) {
            log.error("上传素材失败", ex);
            throw new ApiException("上传素材失败: " + ex.getMessage());
        }
    }

private void validateUploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException("文件不能为空");
        }
    }

    private Media buildMedia(MultipartFile file, Long groupId, byte[] bytes, String contentType) {
        Long normalizedGroupId = groupId != null ? normalizeGroupId(groupId) : null;
        String originalFilename = validateOriginalFilename(file.getOriginalFilename());
        String extension = normalizeExtension(StringUtils.getFilenameExtension(originalFilename));
        String hash = DigestUtil.sha256Hex(bytes);
        MediaType mediaType = MediaType.infer(contentType, extension);

        Media media = new Media();
        media.setGroupId(normalizedGroupId);
        media.setHash(hash);
        media.setOriginalName(originalFilename);
        media.setMediaType(mediaType.name());
        media.setFileSize((long) bytes.length);
        media.setExtension(extension);
        return media;
    }

    private Media getReusableMedia(Media media) {
        Media existing = mediaDao.selectByHash(media.getHash());
        if (existing == null) {
            return null;
        }
        restoreIfDeleted(existing);
        return existing;
    }

    private void restoreIfDeleted(Media media) {
        if (!Integer.valueOf(1).equals(media.getDeleted())) {
            return;
        }
        Media update = new Media();
        update.setId(media.getId());
        update.setDeleted(0);
        mediaDao.updateByPrimaryKeySelective(update);
        media.setDeleted(0);
    }

    private void uploadMediaFiles(Media media, byte[] bytes, String contentType) {
        String url = uploadOriginalFile(media, bytes, contentType);
        String thumbnailUrl = uploadThumbnailIfNecessary(media, bytes);
        media.setUrl(url);
        media.setThumbnailUrl(StrUtil.isBlank(thumbnailUrl) ? url : thumbnailUrl);
    }

    private String uploadOriginalFile(Media media, byte[] bytes, String contentType) {
        String objectName = generateObjectName(MediaType.valueOf(media.getMediaType()), media.getHash(), media.getExtension());
        return validateUrl(objectStorageService.uploadFile(
                bytes,
                objectName,
                contentType));
    }

    private MediaUploadRespVO saveMedia(Media media) {
        try {
            mediaDao.insertSelective(media);
        } catch (DuplicateKeyException ex) {
            Media duplicated = mediaDao.selectByHash(media.getHash());
            if (duplicated != null) {
                return buildUploadResp(duplicated, true);
            }
            throw ex;
        }
        return buildUploadResp(media, false);
    }

    @Override
    public Media get(Long id) {
        return mediaDao.selectByPrimaryKey(id);
    }

    @Override
    public List<Media> page(MediaPageReqVO reqVO) {
        return mediaDao.selectList(reqVO.getGroupId(), reqVO.getMediaType(), reqVO.getKeyword());
    }
@Override
    @Transactional(rollbackFor = Exception.class)
    public int move(Long id, Long groupId) {
        if (get(id) == null) {
            return 0;
        }
        if (groupId == null || groupId == 0) {
            return mediaDao.clearGroupIdByPrimaryKey(id);
        }
        if (groupId < 0) {
            throw new IllegalArgumentException("分组ID不能小于0");
        }
        MediaGroup mediaGroup = mediaGroupDao.selectByPrimaryKey(groupId);
        if (mediaGroup == null) {
            throw new IllegalArgumentException("分组不存在");
        }
        Media media = new Media();
        media.setId(id);
        media.setGroupId(groupId);
        return mediaDao.updateByPrimaryKeySelective(media);
    }
@Override
    public int delete(Long id) {
        return mediaDao.logicDeleteByPrimaryKey(id);
    }

    @Override
    public void download(Long id, HttpServletResponse response) {
        Media media = get(id);
        if (media == null) {
            throw new ApiException("素材不存在");
        }
        String objectName = getObjectName(media);
        try (InputStream inputStream = objectStorageService.downloadFile(objectName);
             OutputStream outputStream = response.getOutputStream()) {
            String fileName = media.getOriginalName() == null
                    ? objectName.substring(objectName.lastIndexOf("/") + 1)
                    : media.getOriginalName();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("下载素材失败", ex);
            throw new ApiException("下载素材失败: " + ex.getMessage());
        }
    }

private Long normalizeGroupId(Long groupId) {
        if (groupId == null || groupId == 0) {
            return null;
        }
        if (groupId < 0) {
            throw new ApiException("分组ID不合法");
        }
        MediaGroup mediaGroup = mediaGroupDao.selectByPrimaryKey(groupId);
        if (mediaGroup == null) {
            throw new ApiException("分组不存在");
        }
        return groupId;
    }

    private String validateOriginalFilename(String originalFilename) {
        if (StrUtil.isBlank(originalFilename)) {
            return "";
        }
        if (originalFilename.length() > 255) {
            throw new ApiException("原始文件名长度不能超过255个字符");
        }
        return originalFilename;
    }

    private String normalizeExtension(String extension) {
        if (StrUtil.isBlank(extension)) {
            return "";
        }
        String normalized = extension.toLowerCase(Locale.ROOT);
        if (normalized.length() > 16) {
            throw new ApiException("文件扩展名长度不能超过16个字符");
        }
        return normalized;
    }

    private String generateObjectName(MediaType mediaType, String hash, String extension) {
        String suffix = StrUtil.isBlank(extension) ? "" : "." + extension;
        return String.format("media/%s/%s/%s/%s%s",
                mediaType.pathName(),
                hash.substring(0, 2),
                hash.substring(2, 4),
                hash,
                suffix);
    }

    private String generateThumbnailObjectName(String hash) {
        return String.format("media/thumb/image/%s/%s/%s_%d.%s",
                hash.substring(0, 2),
                hash.substring(2, 4),
                hash,
                THUMBNAIL_MAX_SIZE,
                THUMBNAIL_EXTENSION);
    }

    private String uploadThumbnailIfNecessary(Media media, byte[] bytes) {
        if (!MediaType.IMAGE.name().equals(media.getMediaType())) {
            return null;
        }
        try {
            byte[] thumbnailBytes = generateThumbnail(bytes);
            if (thumbnailBytes == null) {
                return null;
            }
            String thumbnailObjectName = generateThumbnailObjectName(media.getHash());
            return validateUrl(objectStorageService.uploadFile(thumbnailBytes, thumbnailObjectName, THUMBNAIL_CONTENT_TYPE));
        } catch (Exception ex) {
            log.warn("上传图片缩略图失败", ex);
            return null;
        }
    }

    private byte[] generateThumbnail(byte[] bytes) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BufferedImage source = ImageIO.read(inputStream);
            if (source == null) {
                return null;
            }
            int sourceWidth = source.getWidth();
            int sourceHeight = source.getHeight();
            double scale = Math.min((double) THUMBNAIL_MAX_SIZE / sourceWidth, (double) THUMBNAIL_MAX_SIZE / sourceHeight);
            scale = Math.min(scale, 1D);
            int targetWidth = Math.max(1, (int) Math.round(sourceWidth * scale));
            int targetHeight = Math.max(1, (int) Math.round(sourceHeight * scale));

            BufferedImage target = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = target.createGraphics();
            try {
                graphics.setColor(Color.WHITE);
                graphics.fillRect(0, 0, targetWidth, targetHeight);
                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.drawImage(source, 0, 0, targetWidth, targetHeight, null);
            } finally {
                graphics.dispose();
            }
            return ImageIO.write(target, THUMBNAIL_EXTENSION, outputStream) ? outputStream.toByteArray() : null;
        } catch (Exception ex) {
            log.warn("生成图片缩略图失败", ex);
            return null;
        }
    }

    private String validateUrl(String url) {
        if (url.length() > 255) {
            throw new ApiException("文件访问地址长度不能超过255个字符");
        }
        return url;
    }

    private MediaUploadRespVO buildUploadResp(Media media, boolean reused) {
        return MediaUploadRespVO.builder()
                .id(media.getId())
                .groupId(media.getGroupId())
                .hash(media.getHash())
                .originalName(media.getOriginalName())
                .mediaType(media.getMediaType())
                .fileSize(media.getFileSize())
                .extension(media.getExtension())
                .objectName(getObjectName(media))
                .url(media.getUrl())
                .thumbnailUrl(media.getThumbnailUrl())
                .reused(reused)
                .build();
    }

    private String getObjectName(Media media) {
        if (media == null || StrUtil.isBlank(media.getUrl())) {
            return null;
        }
        String url = media.getUrl();
        String publicUrl = StrUtil.removeSuffix(storageProperties.getPublicUrl(), "/");
        if (StrUtil.isNotBlank(publicUrl) && url.startsWith(publicUrl + "/")) {
            return url.substring(publicUrl.length() + 1);
        }
        String bucketUrl = StrUtil.removeSuffix(storageProperties.getEndpoint(), "/")
                + "/"
                + storageProperties.getBucketName()
                + "/";
        if (url.startsWith(bucketUrl)) {
            return url.substring(bucketUrl.length());
        }
        int mediaPathIndex = url.indexOf("media/");
        if (mediaPathIndex >= 0) {
            return url.substring(mediaPathIndex);
        }
        return url;
    }
}
