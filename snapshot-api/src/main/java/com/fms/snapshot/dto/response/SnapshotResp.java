package com.fms.snapshot.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class SnapshotResp {
    private String version;
    private List<FlagEntry> flags;
}
