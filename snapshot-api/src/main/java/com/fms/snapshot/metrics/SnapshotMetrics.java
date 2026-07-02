package com.fms.snapshot.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Snapshot api metrics.
 * Maps to design section 6: api_ms / cache_hit_pct / data_fresh_ms / resp_kb.
 */
@Component
public class SnapshotMetrics {

    private final MeterRegistry registry;
    private final Counter cacheHits;
    private final Counter cacheMisses;
    private final DistributionSummary respBytes;
    private final AtomicLong lastWriteAtMs = new AtomicLong(0);

    public SnapshotMetrics(MeterRegistry registry) {
        this.registry = registry;

        this.cacheHits = Counter.builder("fms.snap.cache.hits")
                .description("Snapshot cache hits")
                .register(registry);
        this.cacheMisses = Counter.builder("fms.snap.cache.misses")
                .description("Snapshot cache misses")
                .register(registry);
        this.respBytes = DistributionSummary.builder("fms.snap.resp.bytes")
                .description("Snapshot response body size")
                .baseUnit("bytes")
                .register(registry);

        Gauge.builder("fms.snap.cache.hit.pct", this, m -> {
                    double h = m.cacheHits.count();
                    double total = h + m.cacheMisses.count();
                    return total == 0 ? 0 : (h / total) * 100;
                })
                .description("Snapshot cache hit percentage (0-100)")
                .register(registry);

        Gauge.builder("fms.snap.data.fresh.ms", this, m -> {
                    long w = m.lastWriteAtMs.get();
                    return w == 0 ? -1 : System.currentTimeMillis() - w;
                })
                .description("Snapshot data freshness, ms since last write")
                .register(registry);
    }

    public void recordHit() {
        cacheHits.increment();
    }

    public void recordMiss() {
        cacheMisses.increment();
    }

    public void recordRespBytes(long bytes) {
        respBytes.record(bytes);
    }

    public void markWritten() {
        lastWriteAtMs.set(System.currentTimeMillis());
    }

    public Timer.Sample startTimer() {
        return Timer.start(registry);
    }

    public void stopTimer(Timer.Sample sample, String app) {
        sample.stop(registry.timer("fms.snap.api.ms", "app", app));
    }
}