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

    /**
     * for app to set env and app name
     * @param env
     * @param app
     * @return
     */
    public static SDKConfig forApp(String env, String app) {
        return SDKConfig.builder()
                .env(env)
                .app(app)
                .snapshotEndpoint(SDKInfra.snapshotEndpoint())
                .redisHost(SDKInfra.redisHost())
                .redisPort(SDKInfra.redisPort())
                .pollIntervalMs(SDKInfra.pollIntervalMs())
                .initialDelayMs(SDKInfra.initialDelayMs())
                .build();
    }
}