package com.mallease.marketing.converter;

import com.mallease.marketing.model.client.cmd.FlashActivityCmd;
import com.mallease.marketing.model.client.cmd.FlashProductCmd;
import com.mallease.marketing.model.client.cmd.FlashSessionCmd;
import com.mallease.marketing.model.client.vo.FlashActivityVO;
import com.mallease.marketing.model.client.vo.FlashProductVO;
import com.mallease.marketing.model.client.vo.FlashSessionVO;
import com.mallease.marketing.model.data.entity.FlashActivity;
import com.mallease.marketing.model.data.entity.FlashProduct;
import com.mallease.marketing.model.data.entity.FlashSession;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FlashConverter {
    // Activity
    FlashActivityVO activityToVo(FlashActivity flashActivity);

    List<FlashActivityVO> activityListToVoList(List<FlashActivity> list);

    FlashActivity cmdToActivity(FlashActivityCmd cmd);

    // Session
    FlashSessionVO sessionToVo(FlashSession session);

    List<FlashSessionVO> sessionListToVoList(List<FlashSession> list);

    FlashSession cmdToSession(FlashSessionCmd cmd);

    // Product
    FlashProductVO productToVo(FlashProduct product);

    List<FlashProductVO> productListToVoList(List<FlashProduct> list);

    FlashProduct cmdToProduct(FlashProductCmd cmd);
}