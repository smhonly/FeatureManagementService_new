package com.fms.sdk;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class SDKConfig {

    private String env;
    private String app;
    private String snapshotEndpoint;
    private String redisHost;
    private int redisPort;
    private long pollIntervalMs;
    private long initialDelayMs;

    public static SDKConfig forApp(String env, String app) {
        return SDKConfig.builder()
                .env(env)
                .app(app)
                .snapshotEndpoint(env("FMS_SDK_SNAPSHOT_ENDPOINT", "http://localhost:8081"))
                .redisHost(env("FMS_SDK_REDIS_HOST", "localhost"))
                .redisPort(Integer.parseInt(env("FMS_SDK_REDIS_PORT", "6379")))
                .pollIntervalMs(Long.parseLong(env("FMS_SDK_POLL_INTERVAL_MS", "60000")))
                .initialDelayMs(Long.parseLong(env("FMS_SDK_INITIAL_DELAY_MS", "0")))
                .build();
    }

    private static String env(String key, String defaultValue) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? defaultValue : v;
    }
}
