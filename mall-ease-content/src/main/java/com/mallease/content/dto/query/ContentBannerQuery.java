package com.mallease.content.dto.query;

import com.mallease.common.dto.client.BaseQuery;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContentBannerQuery extends BaseQuery {
    private String keyword;
}
