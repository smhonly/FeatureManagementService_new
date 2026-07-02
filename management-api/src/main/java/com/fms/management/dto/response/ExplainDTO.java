package com.fms.management.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ExplainDTO {
    private boolean enabled;
    private String reason;
    private Long flagVersion;
    private Map<String, Object> userContext;
}