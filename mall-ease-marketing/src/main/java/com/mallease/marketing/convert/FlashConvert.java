package com.mallease.marketing.convert;

import com.mallease.marketing.controller.admin.flash.vo.FlashProductReqVO;
import com.mallease.marketing.controller.admin.flash.vo.FlashSessionReqVO;
import com.mallease.marketing.controller.portal.flash.vo.FlashPortalProductRespVO;
import com.mallease.marketing.controller.portal.flash.vo.FlashPortalSessionRespVO;
import com.mallease.marketing.controller.admin.flash.vo.FlashProductRespVO;
import com.mallease.marketing.controller.admin.flash.vo.FlashSessionRespVO;
import com.mallease.marketing.dal.entity.FlashProduct;
import com.mallease.marketing.dal.entity.FlashSession;
import com.mallease.marketing.enums.FlashTimeStatus;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FlashConvert {
    // Session
    @Mapping(target = "timeStatus", expression = "java(resolveTimeStatus(session.getStartTime(), session.getEndTime()))")
    FlashSessionRespVO toFlashSessionResp(FlashSession session);

    List<FlashSessionRespVO> toFlashSessionRespList(List<FlashSession> list);

    FlashSession toFlashSession(FlashSessionReqVO cmd);

    @Mapping(target = "timeStatus", expression = "java(resolveTimeStatus(session.getStartTime(), session.getEndTime()))")
    FlashPortalSessionRespVO toFlashPortalSessionResp(FlashSession session);

    List<FlashPortalSessionRespVO> toFlashPortalSessionRespList(List<FlashSession> list);

    // Product
    FlashProductRespVO toFlashProductResp(FlashProduct product);

    List<FlashProductRespVO> toFlashProductRespList(List<FlashProduct> list);

    FlashProduct toFlashProduct(FlashProductReqVO cmd);

    FlashPortalProductRespVO toFlashPortalProductResp(FlashProductRespVO product);

    List<FlashPortalProductRespVO> toFlashPortalProductRespList(List<FlashProductRespVO> list);

    default Integer resolveTimeStatus(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime)) {
            return FlashTimeStatus.NOT_STARTED.getCode();
        }
        if (now.isAfter(endTime)) {
            return FlashTimeStatus.ENDED.getCode();
        }
        return FlashTimeStatus.ONGOING.getCode();
    }
}
