package com.fms.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fms.management.converter.FlagConverter;
import com.fms.management.dto.request.CreateFlagDTO;
import com.fms.management.dto.request.RolloutDTO;
import com.fms.management.dto.request.TargetingRulesDTO;
import com.fms.management.dto.request.UpdateFlagDTO;
import com.fms.management.dto.response.FlagDTO;
import com.fms.management.dto.response.PageDTO;
import com.fms.management.entity.FlagDO;
import com.fms.management.entity.FlagHistoryDO;
import com.fms.management.exception.BizException;
import com.fms.management.exception.ErrorCode;
import com.fms.management.mapper.FlagHistoryMapper;
import com.fms.management.mapper.FlagMapper;
import com.fms.management.service.FlagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlagServiceImpl extends ServiceImpl<FlagMapper, FlagDO> implements FlagService {

    private static final String STATE_ACTIVE = "active";
    private static final String STATE_ARCHIVED = "archived";

    private final FlagHistoryMapper flagHistoryMapper;

    @Override
    @Transactional
    public FlagDTO create(CreateFlagDTO req, String actor) {
        FlagDO flag = new FlagDO();
        flag.setFlagKey(req.getFlagKey());
        flag.setEnv(req.getEnv());
        flag.setApp(req.getApp());
        flag.setFlagType(req.getFlagType());
        flag.setState(STATE_ACTIVE);
        flag.setDefinition(req.getDefinition());
        flag.setDescription(req.getDescription());
        flag.setCreatedBy(actor);
        flag.setUpdatedBy(actor);

        try {
            saveOrFail(flag);
        } catch (DuplicateKeyException e) {
            throw new BizException(ErrorCode.FLAG_KEY_CONFLICT, req.getFlagKey());
        }
        recordHistory(flag);
        log.info("Created flag '{}'", flag.getFlagKey());
        return FlagConverter.toDTO(flag);
    }

    @Override
    @Transactional
    public FlagDTO update(String flagKey, UpdateFlagDTO req, String actor) {
        FlagDO existing = find(flagKey);

        if (req.getDefinition() != null) {
            existing.setDefinition(req.getDefinition());
        }
        if (req.getDescription() != null) {
            existing.setDescription(req.getDescription());
        }
        existing.setUpdatedBy(actor);

        FlagDO saved = saveOrFail(existing);
        recordHistory(saved);
        log.info("Updated flag '{}'", flagKey);
        return FlagConverter.toDTO(getById(saved.getId()));
    }

    @Override
    @Transactional
    public FlagDTO archive(String flagKey, String actor) {
        FlagDO existing = find(flagKey);
        existing.setState(STATE_ARCHIVED);
        existing.setUpdatedBy(actor);
        FlagDO saved = saveOrFail(existing);
        recordHistory(saved);
        log.info("Archived flag '{}'", flagKey);
        return FlagConverter.toDTO(getById(saved.getId()));
    }

    @Override
    public FlagDTO get(String flagKey) {
        return FlagConverter.toDTO(find(flagKey));
    }

    @Override
    public PageDTO<FlagDTO> search(String env, String app, String state, String flagType, long current, long size) {
        LambdaQueryWrapper<FlagDO> qw = new LambdaQueryWrapper<>();
        if (env != null && !env.isBlank()) {
            qw.eq(FlagDO::getEnv, env);
        }
        if (app != null && !app.isBlank()) {
            qw.eq(FlagDO::getApp, app);
        }
        if (state != null && !state.isBlank()) {
            qw.eq(FlagDO::getState, state);
        }
        if (flagType != null && !flagType.isBlank()) {
            qw.eq(FlagDO::getFlagType, flagType);
        }
        qw.orderByDesc(FlagDO::getUpdatedAt);

        IPage<FlagDO> page = page(new Page<>(current, size), qw);
        List<FlagDTO> rows = page.getRecords().stream().map(FlagConverter::toDTO).toList();
        return new PageDTO<>(page.getTotal(), page.getCurrent(), page.getSize(), rows);
    }

    @Override
    @Transactional
    public FlagDTO updateRollout(String flagKey, RolloutDTO req, String actor) {
        FlagDO existing = find(flagKey);
        if (!"pct".equals(existing.getFlagType())) {
            throw new BizException(ErrorCode.INVALID_REQUEST,
                    "Rollout update is only for pct flags.");
        }

        ObjectNode def = toObject(existing.getDefinition());
        def.put("type", "pct");
        def.put("pct", req.getPct());
        if (req.getSalt() != null) {
            def.put("salt", req.getSalt());
        }
        if (req.getRules() != null) {
            def.set("rules", req.getRules());
        }

        existing.setDefinition(def);
        existing.setUpdatedBy(actor);
        FlagDO saved = saveOrFail(existing);
        recordHistory(saved);
        log.info("Updated rollout '{}'", flagKey);
        return FlagConverter.toDTO(getById(saved.getId()));
    }

    @Override
    @Transactional
    public FlagDTO updateTargeting(String flagKey, TargetingRulesDTO req, String actor) {
        FlagDO existing = find(flagKey);
        if (!"targeting".equals(existing.getFlagType())
                && !"pct".equals(existing.getFlagType())) {
            throw new BizException(ErrorCode.INVALID_REQUEST,
                    "Invalid flag type '" + existing.getFlagType() + "'");
        }

        ObjectNode def = toObject(existing.getDefinition());
        def.put("type", existing.getFlagType());
        def.set("rules", req.getRules());

        existing.setDefinition(def);
        existing.setUpdatedBy(actor);
        FlagDO saved = saveOrFail(existing);
        recordHistory(saved);
        log.info("Updated targeting '{}'", flagKey);
        return FlagConverter.toDTO(getById(saved.getId()));
    }

    private void recordHistory(FlagDO f) {
        FlagHistoryDO h = new FlagHistoryDO();
        h.setFlagKey(f.getFlagKey());
        h.setEnv(f.getEnv());
        h.setApp(f.getApp());
        h.setVersion(f.getVersion());
        h.setFlagType(f.getFlagType());
        h.setState(f.getState());
        h.setDefinition(f.getDefinition());
        h.setUpdatedAt(f.getUpdatedAt());
        h.setUpdatedBy(f.getUpdatedBy());
        flagHistoryMapper.insert(h);
    }

    private FlagDO find(String flagKey) {
        FlagDO flag = lambdaQuery().eq(FlagDO::getFlagKey, flagKey).one();
        if (flag == null) {
            throw new BizException(ErrorCode.FLAG_NOT_FOUND, flagKey);
        }
        return flag;
    }

    private FlagDO saveOrFail(FlagDO flag) {
        save(flag);
        //after flag record saved, CDC will detect and send kafka msg to snapshot-api.
        //todo: CDC is the infra task, no code changes here.
        return flag;
    }

    private ObjectNode toObject(JsonNode n) {
        return n instanceof ObjectNode o ? o : JsonNodeFactory.instance.objectNode();
    }
}