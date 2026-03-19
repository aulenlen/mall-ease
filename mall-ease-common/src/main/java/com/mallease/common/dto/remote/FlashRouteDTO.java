package com.mallease.common.dto.remote;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlashRouteDTO {

    private Long sessionId;

    private Integer routeType;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    public boolean isExpired(LocalDateTime now) {
        return endTime != null && endTime.isBefore(now);
    }
}
