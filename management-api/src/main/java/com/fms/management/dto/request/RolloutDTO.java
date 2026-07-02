package com.fms.management.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RolloutDTO {

    @NotNull
    @Min(0)
    @Max(100)
    private Integer pct;

    private String salt;

    private JsonNode rules;
}
