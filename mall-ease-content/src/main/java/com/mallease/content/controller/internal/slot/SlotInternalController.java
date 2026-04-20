package com.mallease.content.controller.internal.slot;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.ArticleDTO;
import com.mallease.common.dto.remote.SlotCardDTO;
import com.mallease.common.dto.remote.SlotRenderDTO;
import com.mallease.content.constant.ContentStatusConstants;
import com.mallease.content.convert.article.ArticleConvert;
import com.mallease.content.convert.slot.SlotConvert;
import com.mallease.content.dal.entity.Article;
import com.mallease.content.dal.entity.Slot;
import com.mallease.content.dal.entity.SlotItem;
import com.mallease.content.service.article.ArticleExtrasCodec;
import com.mallease.content.service.article.ArticleService;
import com.mallease.content.service.slot.SlotItemService;
import com.mallease.content.service.slot.SlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Tag(name = "槽位内部接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/content/slot/internal")
public class SlotInternalController {

    private final SlotService slotService;
    private final SlotItemService slotItemService;
    private final SlotConvert slotConvert;
    private final ArticleService articleService;
    private final ArticleConvert articleConvert;
    private final ArticleExtrasCodec articleExtrasCodec;

    @Operation(summary = "获取槽位渲染数据")
    @GetMapping("/render")
    public R<SlotRenderDTO> render(
            @Parameter(description = "槽位编码") @RequestParam String slotCode,
            @Parameter(description = "返回数量，默认10") @RequestParam(defaultValue = "10") Integer limit) {
        Slot slot = slotService.getByCode(slotCode);
        if (slot == null || !Integer.valueOf(ContentStatusConstants.ENABLE_STATUS_ENABLED).equals(slot.getStatus())) {
            return R.success(SlotRenderDTO.builder()
                    .slotCode(slotCode)
                    .cards(Collections.emptyList())
                    .articles(Collections.emptyList())
                    .build());
        }

        if ("SWIPER".equalsIgnoreCase(slot.getRenderType())) {
            List<SlotItem> slotItemList = slotItemService.listPublishedCards(slotCode, limit);
            return R.success(SlotRenderDTO.builder()
                    .slotCode(slotCode)
                    .renderType(slot.getRenderType())
                    .cards(slotConvert.toSlotCardDTOList(slotItemList))
                    .articles(Collections.emptyList())
                    .build());
        }

        List<Article> articleList = articleService.listPublishedBySlotCode(slotCode, limit);
        Map<Long, List<Long>> spuIdsMap = articleService.listSpuIdsMap(articleList.stream().map(Article::getId).toList());
        return R.success(SlotRenderDTO.builder()
                .slotCode(slotCode)
                .renderType(slot.getRenderType())
                .cards(Collections.emptyList())
                .articles(articleConvert.toArticleRemoteList(articleList, spuIdsMap, articleExtrasCodec))
                .build());
    }
}
