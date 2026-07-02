package com.fms.management.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fms.management.dto.request.CreateFlagDTO;
import com.fms.management.dto.request.RolloutDTO;
import com.fms.management.dto.request.TargetingRulesDTO;
import com.fms.management.dto.request.UpdateFlagDTO;
import com.fms.management.dto.response.FlagDTO;
import com.fms.management.dto.response.PageDTO;
import com.fms.management.entity.FlagDO;

public interface FlagService extends IService<FlagDO> {

    FlagDTO create(CreateFlagDTO req, String actor);

    FlagDTO update(String flagKey, UpdateFlagDTO req, String actor);

    FlagDTO archive(String flagKey, String actor);

    FlagDTO get(String flagKey);

    PageDTO<FlagDTO> search(String env, String app, String state, String flagType, long current, long size);

    FlagDTO updateRollout(String flagKey, RolloutDTO req, String actor);

    FlagDTO updateTargeting(String flagKey, TargetingRulesDTO req, String actor);
}