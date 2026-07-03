package com.fms.snapshot.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Updates snapshot on flag change events.
 *
 * @author matt.shi
 * @date 2026/07/02
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class FlagChangeListener {

    private final ObjectMapper objectMapper;
    private final SnapshotStore snapshotStore;

    @KafkaListener(topics = "${fms.kafka.topic:fms.snapshot.changed}",
            groupId = "${spring.kafka.consumer.group-id:snapshot-api}")
    public void onEvent(String payload, Acknowledgment ack) throws JsonProcessingException {
        if (payload == null || payload.isBlank()) {
            ack.acknowledge();
            return;
        }
        FlagChangeEvent e = objectMapper.readValue(payload, FlagChangeEvent.class);
        if (e.getEnv() == null || e.getApp() == null || e.getFlagKey() == null) {
            ack.acknowledge();
            return;
        }

        //exception escapes -> DefaultErrorHandler retries; ack never called
        snapshotStore.apply(e);
        ack.acknowledge();
    }
}