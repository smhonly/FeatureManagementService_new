package com.fms.management.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fms.management.entity.FlagDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlagChangePublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${fms.kafka.topic:fms.snapshot.changed}")
    private String topic;

    @Value("${fms.kafka.producer-enabled:true}")
    private boolean producerEnabled;

    public void publish(FlagDO flag, String op) {
        if (!producerEnabled) {
            return;
        }
        try {
            FlagChangeEvent event = new FlagChangeEvent();
            event.setEnv(flag.getEnv());
            event.setApp(flag.getApp());
            event.setFlagKey(flag.getFlagKey());
            event.setDefinition(flag.getDefinition());
            event.setVersion(flag.getVersion());
            event.setOp(op);

            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, flag.getFlagKey(), payload);
            log.info("Published flag change event, key={}, op={}", flag.getFlagKey(), op);
        } catch (Exception e) {
            log.error("Failed to publish flag change event, key={}", flag.getFlagKey(), e);
        }
    }
}
