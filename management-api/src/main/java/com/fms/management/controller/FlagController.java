package com.fms.management.controller;

import com.fms.management.dto.request.CreateFlagDTO;
import com.fms.management.dto.request.RolloutDTO;
import com.fms.management.dto.request.TargetingRulesDTO;
import com.fms.management.dto.request.UpdateFlagDTO;
import com.fms.management.dto.response.FlagDTO;
import com.fms.management.dto.response.PageDTO;
import com.fms.management.service.FlagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/flags")
@RequiredArgsConstructor
public class FlagController {

    private final FlagService flagService;

    @PostMapping
    public ResponseEntity<FlagDTO> create(
            @Valid @RequestBody CreateFlagDTO req,
            @RequestHeader(value = "X-Actor", required = false) String actor) {
        return ResponseEntity.ok(flagService.create(req, nullSafe(actor)));
    }

    @PutMapping("/{key}")
    public ResponseEntity<FlagDTO> update(
            @PathVariable("key") String key,
            @Valid @RequestBody UpdateFlagDTO req,
            @RequestHeader(value = "X-Actor", required = false) String actor) {
        return ResponseEntity.ok(flagService.update(key, req, nullSafe(actor)));
    }

    @GetMapping
    public ResponseEntity<PageDTO<FlagDTO>> search(
            @RequestParam(value = "env",     required = false) String env,
            @RequestParam(value = "app",     required = false) String app,
            @RequestParam(value = "state",    required = false) String state,
            @RequestParam(value = "flagType", required = false) String flagType,
            @RequestParam(value = "current",  defaultValue = "1")  long current,
            @RequestParam(value = "size",     defaultValue = "20") long size) {
        return ResponseEntity.ok(flagService.search(env, app, state, flagType, current, size));
    }

    @GetMapping("/{key}")
    public ResponseEntity<FlagDTO> get(@PathVariable("key") String key) {
        return ResponseEntity.ok(flagService.get(key));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<FlagDTO> archive(
            @PathVariable("key") String key,
            @RequestHeader(value = "X-Actor", required = false) String actor) {
        return ResponseEntity.ok(flagService.archive(key, nullSafe(actor)));
    }

    @PostMapping("/{key}/rollout")
    public ResponseEntity<FlagDTO> rollout(
            @PathVariable("key") String key,
            @Valid @RequestBody RolloutDTO req,
            @RequestHeader(value = "X-Actor", required = false) String actor) {
        return ResponseEntity.ok(flagService.updateRollout(key, req, nullSafe(actor)));
    }

    @PostMapping("/{key}/targeting")
    public ResponseEntity<FlagDTO> targeting(
            @PathVariable("key") String key,
            @Valid @RequestBody TargetingRulesDTO req,
            @RequestHeader(value = "X-Actor", required = false) String actor) {
        return ResponseEntity.ok(flagService.updateTargeting(key, req, nullSafe(actor)));
    }

    private static String nullSafe(String s) {
        return s == null || s.isBlank() ? "system" : s;
    }
}