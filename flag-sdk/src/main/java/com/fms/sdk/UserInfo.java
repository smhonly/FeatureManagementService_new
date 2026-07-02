package com.fms.sdk;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * User info passed to isEnabled().
 *
 * @author matt.shi
 * @date 2026/07/02
 **/
@Data
@Builder
public class UserInfo {

    private String userId;
    private String region;
    private String tenant;
    private String role;

    @Builder.Default
    private Map<String, Object> attrs = new HashMap<>();

    public UserInfo attr(String key, Object value) {
        attrs.put(key, value);
        return this;
    }
}