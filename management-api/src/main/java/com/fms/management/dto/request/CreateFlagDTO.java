package com.fms.management.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateFlagDTO {

    @NotBlank
    @Size(max = 128)
    private String flagKey;

    @NotBlank
    @Size(max = 32)
    private String env;

    @NotBlank
    @Size(max = 64)
    private String app;

    @NotBlank
    @Pattern(regexp = "boolean|pct|targeting")
    private String flagType;

    @NotNull
    private JsonNode definition;

    @Size(max = 512)
    private String description;
}
