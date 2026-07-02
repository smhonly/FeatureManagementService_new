package com.fms.management.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TargetingRulesDTO {

    @NotNull
    private JsonNode rules;
}
