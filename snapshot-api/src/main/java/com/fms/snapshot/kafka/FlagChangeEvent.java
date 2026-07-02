package com.fms.snapshot.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * Flag change event from Kafka.
 *
 * @author matt.shi
 * @date 2026/07/02
 **/
@Data
public class FlagChangeEvent {
    private String env;
    private String app;
    private String flagKey;
    private JsonNode definition;
    private Long version;
    private String op;
}
