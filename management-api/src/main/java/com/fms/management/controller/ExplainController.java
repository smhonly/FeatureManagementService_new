package com.fms.management.controller;

import com.fms.management.dto.request.ExplainRequestDTO;
import com.fms.management.dto.response.ExplainDTO;
import com.fms.management.service.ExplainService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/explain")
@RequiredArgsConstructor
public class ExplainController {

    private final ExplainService explainService;

    @PostMapping
    public ResponseEntity<ExplainDTO> explain(@Valid @RequestBody ExplainRequestDTO req) {
        return ResponseEntity.ok(explainService.explain(
                req.getFlag(), req.getEnv(), req.getApp(), req.getUserinfo(), req.getAt()));
    }
}