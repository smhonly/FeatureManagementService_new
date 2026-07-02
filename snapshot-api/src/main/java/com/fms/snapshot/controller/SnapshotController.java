package com.fms.snapshot.controller;

import com.fms.snapshot.dto.response.SnapshotResp;
import com.fms.snapshot.service.SnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Snapshot read API.
 *
 * @author matt.shi
 * @date 2026/07/02
 **/
@RestController
@RequestMapping("/api/v1/snapshot")
@RequiredArgsConstructor
public class SnapshotController {

    private final SnapshotService snapshotService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public SnapshotResp getSnapshot(
            @RequestParam("Env") String env,
            @RequestParam("App") String app) {
        return snapshotService.get(env, app);
    }
}
