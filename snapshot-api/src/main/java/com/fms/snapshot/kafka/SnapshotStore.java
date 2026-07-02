package com.fms.snapshot.kafka;

import com.fms.snapshot.dto.response.FlagEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Snapshot Store
 *
 * @author matt.shi
 * @date 2026/07/02
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotStore {

    private final SnapshotRedisCache redisCache;
    private final Map<String, Map<String, FlagEntry>> snapshots = new ConcurrentHashMap<>();

    public void apply(FlagChangeEvent e) {
        String scope = e.getEnv() + ":" + e.getApp();
        Map<String, FlagEntry> m = snapshots.computeIfAbsent(scope, k -> new ConcurrentHashMap<>());

        //idempotency, find exist flag
        FlagEntry existing = m.get(e.getFlagKey());
        //compare version
        if (existing != null && e.getVersion() != null && existing.getVersion() != null
                && existing.getVersion() >= e.getVersion()) {
            log.info("skip stale event, flagKey={} current={} incoming={}",
                    e.getFlagKey(), existing.getVersion(), e.getVersion());
            return;
        }

        if ("archived".equals(e.getOp())) {
            m.remove(e.getFlagKey());
        } else {
            FlagEntry entry = new FlagEntry();
            entry.setKey(e.getFlagKey());
            entry.setDefinition(e.getDefinition());
            entry.setVersion(e.getVersion());
            m.put(e.getFlagKey(), entry);
        }
        log.info("apply flag change, env={}, app={}, flagKey={}, op={}",
                e.getEnv(), e.getApp(), e.getFlagKey(), e.getOp());
        redisCache.write(e.getEnv(), e.getApp(), m);
    }
}