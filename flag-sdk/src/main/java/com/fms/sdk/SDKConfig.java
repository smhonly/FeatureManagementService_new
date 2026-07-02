package com.fms.sdk;

import lombok.Builder;
import lombok.Data;

/**
 * SDK config.
 *
 * @author matt.shi
 * @date 2026/07/02
 **/
@Data
@Builder
public class SDKConfig {

    private String env;
    private String app;
    private String snapshotEndpoint;
    private String redisHost;
    private int redisPort;
    private long pollIntervalMs;
    private long initialDelayMs;

    public static SDKConfig defaults(String env, String app, String snapshotEndpoint, String redisHost, int redisPort) {
        return SDKConfig.builder()
                .env(env)
                .app(app)
                .snapshotEndpoint(snapshotEndpoint)
                .redisHost(redisHost)
                .redisPort(redisPort)
                .pollIntervalMs(60_000L)
                .initialDelayMs(0L)
                .build();
    }
}