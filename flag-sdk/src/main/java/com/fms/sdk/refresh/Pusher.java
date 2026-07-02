package com.fms.sdk.refresh;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fms.sdk.SDKConfig;
import com.fms.sdk.cache.LocalCache;
import com.fms.sdk.model.Snapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

/**
 * Subscribes to redis;
 * each message saved into LocalCache.
 *
 * @author matt.shi
 * @date 2026/07/02
 **/
@Slf4j
@RequiredArgsConstructor
public class Pusher {

    private final SDKConfig config;
    private final LocalCache cache;
    private final ObjectMapper mapper = new ObjectMapper();

    private volatile boolean running;
    private JedisPool pool;
    private Thread worker;

    public void start() {
        if (running) {
            return;
        }
        running = true;
        pool = new JedisPool(config.getRedisHost(), config.getRedisPort());
        String channel = channel(config);
        log.info("redis pusher subscribe channel={}", channel);
        worker = new Thread(() -> loop(channel), "sdk-redis-pusher");
        worker.setDaemon(true);
        worker.start();
    }

    public void stop() {
        running = false;
        if (pool != null) {
            pool.destroy();
        }
        if (worker != null) {
            worker.interrupt();
        }
    }

    private void loop(String channel) {
        while (running) {
            try (Jedis j = pool.getResource()) {
                j.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String ch, String msg) {
                        apply(msg);
                    }
                }, channel);
            } catch (Exception e) {
                if (running) {
                    log.warn("redis subscribe lost, retry in 2s", e);
                    sleep(2000);
                }
            }
        }
    }

    private void apply(String json) {
        try {
            Snapshot s = mapper.readValue(json, Snapshot.class);
            cache.replace(s);
        } catch (Exception e) {
            log.warn("apply snapshot failed", e);
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static String channel(SDKConfig config) {
        return "fms:snap:changed:" + config.getEnv() + ":" + config.getApp();
    }
}