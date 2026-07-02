package com.fms.sdk;

import com.fms.sdk.cache.LocalCache;
import com.fms.sdk.evaluator.Evaluator;
import com.fms.sdk.model.Flag;
import com.fms.sdk.refresh.Poller;
import com.fms.sdk.refresh.Pusher;
import com.fms.sdk.refresh.SnapshotClient;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;


/**
 * Flag client entry point.
 *
 * @author matt.shi
 * @date 2026/07/02
 **/
@Slf4j
public class SDKClient {

    private final SDKConfig config;
    private final LocalCache cache;
    private final Pusher pusher;
    private final Poller poller;
    private final Evaluator evaluator = new Evaluator();

    public SDKClient(SDKConfig config) {
        this.config = config;
        this.cache = new LocalCache();
        this.pusher = new Pusher(config, cache);
        this.poller = new Poller(config, cache, new SnapshotClient(config));
    }

    public void start() {
        poller.start();
        pusher.start();
    }

    public void stop() {
        poller.stop();
        pusher.stop();
    }

    public boolean isEnabled(String flagKey, UserInfo user) {
        long start = System.currentTimeMillis();
        Flag flag = cache.get(flagKey);
        boolean hit = flag != null;
        boolean result = hit && evaluator.isEnabled(flag, ctx(user));
        log.info("eval flag={} userId={} hit={} result={} time={}",
                flagKey, user.getUserId(), hit, result, (System.currentTimeMillis() - start));
        return result;
    }

    //add more attr like env and app
    private EvalContext ctx(UserInfo user) {
        Map<String, Object> attrs = user == null || user.getAttrs() == null ? new HashMap<>() : new HashMap<>(user.getAttrs());
        return EvalContext.builder()
                .userId(user == null ? null : user.getUserId())
                .env(config.getEnv())
                .app(config.getApp())
                .region(user == null ? null : user.getRegion())
                .tenant(user == null ? null : user.getTenant())
                .userAttrs(attrs)
                .build();
    }
}