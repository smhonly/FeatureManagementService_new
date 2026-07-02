package com.fms.sdk;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Per-call eval context: fixed attrs + user attrs.
 *
 * @author matt.shi
 * @date 2026/07/02
 **/
@Data
@Builder
public class EvalContext {

    private String userId;
    private String env;
    private String app;
    private String region;
    private String tenant;

    @Builder.Default
    private Map<String, Object> userAttrs = new HashMap<>();

    public Object attr(String name) {
        if ("userId".equals(name)) {
            return userId;
        }
        if ("env".equals(name)) {
            return env;
        }
        if ("app".equals(name)) {
            return app;
        }
        if ("region".equals(name)) {
            return region;
        }
        if ("tenant".equals(name)) {
            return tenant;
        }
        return userAttrs.get(name);
    }
}