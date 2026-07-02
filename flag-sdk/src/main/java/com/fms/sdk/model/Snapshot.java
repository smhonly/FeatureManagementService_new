package com.fms.sdk.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Snapshot pulled from snapshot-api.
 *
 * @author matt.shi
 * @date 2026/07/02
 **/
@Data
public class Snapshot {
    private String version;
    private List<Flag> flags = new ArrayList<>();
}