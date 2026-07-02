package com.fms.snapshot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fms.snapshot.dto.response.SnapshotResp;
import com.fms.snapshot.exception.BizException;
import com.fms.snapshot.exception.ErrorCode;
import com.fms.snapshot.exception.SystemErrorException;
import com.fms.snapshot.metrics.SnapshotMetrics;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Snapshot read service.
 *
 * @author matt.shi
 * @date 2026/07/02
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotService {

    //fms=FeatureManagementService; snap=snapshot
    private static final String KEY_PREFIX = "fms:snap:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final SnapshotMetrics metrics;

    public SnapshotResp get(String env, String app) {
        if (StringUtils.isBlank(env) || StringUtils.isBlank(app)) {
            throw new BizException(ErrorCode.INVALID_REQUEST, "env and app are required");
        }
        Timer.Sample sample = metrics.startTimer();
        try {
            log.info("call getSnapshot, env={}, app={}", env, app);
            String key = KEY_PREFIX + env + ":" + app;
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) {
                metrics.recordMiss();
                log.info("snapshot not found, key={}", key);
                throw new BizException(ErrorCode.SNAPSHOT_NOT_FOUND, env + "/" + app);
            }
            metrics.recordHit();
            metrics.recordRespBytes(json.length());
            return objectMapper.readValue(json, SnapshotResp.class);
        } catch (JsonProcessingException e) {
            log.error("snapshot payload malformed, env={}, app={}", env, app, e);
            throw new SystemErrorException(ErrorCode.INTERNAL_ERROR,
                    "snapshot payload malformed: " + env + "/" + app, e);
        } finally {
            metrics.stopTimer(sample, app);
        }
    }
}