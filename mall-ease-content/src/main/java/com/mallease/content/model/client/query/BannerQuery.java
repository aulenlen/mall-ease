package com.mallease.content.model.client.query;

import com.mallease.common.dto.client.BaseQuery;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class BannerQuery extends BaseQuery {
    private String keyword;
}
