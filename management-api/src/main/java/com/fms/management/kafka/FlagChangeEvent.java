package com.fms.management.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class FlagChangeEvent {

    private String env;
    private String app;
    private String flagKey;
    private JsonNode definition;
    private Long version;
    private String op;
}
