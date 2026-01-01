package com.mallease.cms.dto.query;

import com.mallease.common.dto.query.BaseQuery;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class CmsBannerQuery extends BaseQuery {
    private String keyword;
}
