package com.fms.snapshot.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class FlagEntry {
    private String key;
    private JsonNode definition;
    private Long version;
}
