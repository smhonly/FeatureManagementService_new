package com.fms.snapshot.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fms.snapshot.dto.response.FlagEntry;
import com.fms.snapshot.dto.response.SnapshotResp;
import com.fms.snapshot.metrics.SnapshotMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

/**
 * Persists snapshot to Redis.
 *
 * @author matt.shi
 * @date 2026/07/02
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotRedisCache {

    private static final String KEY_PREFIX = "fms:snap:";
    //SDK subscribes to this channel to get push updates
    private static final String CHANNEL_PREFIX = "fms:snap:changed:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final SnapshotMetrics metrics;

    public void write(String env, String app, Map<String, FlagEntry> flags) {
        SnapshotResp resp = new SnapshotResp();
        long max = flags.values().stream()
                .map(e -> e.getVersion() == null ? 0L : e.getVersion())
                .max(Long::compare)
                .orElse(0L);
        resp.setVersion("v" + max);
        resp.setFlags(new ArrayList<>(flags.values()));
        String json;
        try {
            json = objectMapper.writeValueAsString(resp);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("snapshot serialize failed, env=" + env + ", app=" + app, e);
        }
        String key = KEY_PREFIX + env + ":" + app;
        redisTemplate.opsForValue().set(key, json);
        metrics.markWritten();
        //push to SDK subscribers
        redisTemplate.convertAndSend(CHANNEL_PREFIX + env + ":" + app, json);
    }
}