package com.fms.management.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlagDTO {

    private Long id;
    private String flagKey;
    private String flagType;
    private String state;
    private JsonNode definition;
    private Long version;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
