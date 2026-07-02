package com.fms.sdk.refresh;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fms.sdk.SDKConfig;
import com.fms.sdk.model.Flag;
import com.fms.sdk.model.Snapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;

/**
 * Calls snapshot-api over HTTP and parses the response.
 *
 * @author matt.shi
 * @date 2026/07/02
 **/
@Slf4j
@RequiredArgsConstructor
public class SnapshotClient {

    private final SDKConfig config;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    public Snapshot fetch() throws Exception {
        String url = config.getSnapshotEndpoint()
                + "?env=" + config.getEnv()
                + "&app=" + config.getApp();
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(3))
                .GET()
                .build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            throw new RuntimeException("snapshot fetch http " + resp.statusCode());
        }
        return parse(resp.body());
    }

    private Snapshot parse(String json) {
        try {
            Snapshot snapshot = objectMapper.readValue(json, Snapshot.class);
            if (snapshot.getFlags() == null) {
                snapshot.setFlags(new ArrayList<>());
            }
            for (Flag f : snapshot.getFlags()) {
                Flag parsed = Flag.fromDefinition(f.getKey(), f.getDefinition());
                f.setType(parsed.getType());
                f.setValue(parsed.getValue());
                f.setPct(parsed.getPct());
                f.setSalt(parsed.getSalt());
                f.setRules(parsed.getRules());
            }
            return snapshot;
        } catch (Exception e) {
            log.error("snapshot parse failed", e);
            return null;
        }
    }
}