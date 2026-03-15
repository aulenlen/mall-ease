package com.mallease.marketing.converter;

import com.mallease.marketing.model.client.cmd.FlashProductCmd;
import com.mallease.marketing.model.client.cmd.FlashSessionCmd;
import com.mallease.marketing.model.client.vo.FlashPortalProductVO;
import com.mallease.marketing.model.client.vo.FlashPortalSessionVO;
import com.mallease.marketing.model.client.vo.FlashProductVO;
import com.mallease.marketing.model.client.vo.FlashSessionVO;
import com.mallease.marketing.model.data.entity.FlashProduct;
import com.mallease.marketing.model.data.entity.FlashSession;
import com.mallease.marketing.model.enums.FlashTimeStatus;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FlashConverter {
    // Session
    @Mapping(target = "timeStatus", expression = "java(resolveTimeStatus(session.getStartTime(), session.getEndTime()))")
    FlashSessionVO sessionToVo(FlashSession session);

    List<FlashSessionVO> sessionListToVoList(List<FlashSession> list);

    FlashSession cmdToSession(FlashSessionCmd cmd);

    @Mapping(target = "timeStatus", expression = "java(resolveTimeStatus(session.getStartTime(), session.getEndTime()))")
    FlashPortalSessionVO sessionToPortalVo(FlashSession session);

    List<FlashPortalSessionVO> sessionListToPortalVoList(List<FlashSession> list);

    // Product
    FlashProductVO productToVo(FlashProduct product);

    List<FlashProductVO> productListToVoList(List<FlashProduct> list);

    FlashProduct cmdToProduct(FlashProductCmd cmd);

    FlashPortalProductVO productVoToPortalVo(FlashProductVO product);

    List<FlashPortalProductVO> productVoListToPortalVoList(List<FlashProductVO> list);

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
