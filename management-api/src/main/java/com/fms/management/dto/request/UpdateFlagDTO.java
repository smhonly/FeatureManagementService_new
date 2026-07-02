package com.fms.management.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateFlagDTO {

    @NotNull
    private Long version;

    private JsonNode definition;

    @Size(max = 512)
    private String description;
}
