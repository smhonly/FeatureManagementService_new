package com.fms.sdk.refresh;

import com.fms.sdk.SDKConfig;
import com.fms.sdk.cache.LocalCache;
import com.fms.sdk.model.Snapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Polls snapshot-api over HTTP and updates LocalCache.
 *
 * @author matt.shi
 * @date 2026/07/02
 **/
@Slf4j
@RequiredArgsConstructor
public class Poller {

    private final SDKConfig config;
    private final LocalCache cache;
    private final SnapshotClient client;

    private ScheduledExecutorService scheduler;

    public void start() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "sdk-snapshot-poller");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(this::handle,
                config.getInitialDelayMs(), config.getPollIntervalMs(), TimeUnit.MILLISECONDS);
        log.info("snapshot poller started, env={}, app={}", config.getEnv(), config.getApp());
    }

    public void stop() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    private void handle() {
        try {
            Snapshot s = client.fetch();
            if (s != null) {
                cache.replace(s);
            }
        } catch (Exception e) {
            log.warn("snapshot poll failed", e);
        }
    }
}