package com.fms.sdk.cache;

import com.fms.sdk.model.Flag;
import com.fms.sdk.model.Snapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Local mem snapshot
 *
 * @author matt.shi
 * @date 2026/07/02
 **/
public class LocalCache {

    private volatile Snapshot snapshot;
    private volatile Map<String, Flag> index = new HashMap<>();
    private volatile long lastUpdatedMs = 0L;

    public void replace(Snapshot snapshot) {
        Map<String, Flag> next = new HashMap<>();
        if (snapshot != null && snapshot.getFlags() != null) {
            for (Flag f : snapshot.getFlags()) {
                if (f != null && f.getKey() != null) {
                    next.put(f.getKey(), f);
                }
            }
        }
        this.index = next;
        this.snapshot = snapshot;
        this.lastUpdatedMs = System.currentTimeMillis();
    }

    public Snapshot current() {
        return snapshot;
    }

    public Flag get(String key) {
        return index.get(key);
    }

    public long ageMs() {
        return System.currentTimeMillis() - lastUpdatedMs;
    }
}