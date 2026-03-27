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
    FlashSessionRespVO sessionToRespVO(FlashSession session);

    List<FlashSessionRespVO> sessionListToRespVOList(List<FlashSession> list);

    FlashSession reqVOToSession(FlashSessionReqVO cmd);

    @Mapping(target = "timeStatus", expression = "java(resolveTimeStatus(session.getStartTime(), session.getEndTime()))")
    FlashPortalSessionRespVO sessionToPortalRespVO(FlashSession session);

    List<FlashPortalSessionRespVO> sessionListToPortalRespVOList(List<FlashSession> list);

    // Product
    FlashProductRespVO productToRespVO(FlashProduct product);

    List<FlashProductRespVO> productListToRespVOList(List<FlashProduct> list);

    FlashProduct reqVOToProduct(FlashProductReqVO cmd);

    FlashPortalProductRespVO productRespVOToPortalRespVO(FlashProductRespVO product);

    List<FlashPortalProductRespVO> productRespVOListToPortalRespVOList(List<FlashProductRespVO> list);

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
