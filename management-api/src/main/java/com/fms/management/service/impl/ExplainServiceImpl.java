package com.fms.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fms.management.dto.response.ExplainDTO;
import com.fms.management.entity.FlagHistoryDO;
import com.fms.management.exception.BizException;
import com.fms.management.exception.ErrorCode;
import com.fms.management.mapper.FlagHistoryMapper;
import com.fms.management.service.ExplainService;
import com.fms.sdk.EvalContext;
import com.fms.sdk.UserInfo;
import com.fms.sdk.evaluator.Evaluator;
import com.fms.sdk.model.Flag;
import com.fms.sdk.utils.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExplainServiceImpl implements ExplainService {

    private final FlagHistoryMapper flagHistoryMapper;
    private final Evaluator evaluator = new Evaluator();

    @Override
    public ExplainDTO explain(String flagKey, String env, String app, UserInfo userinfo, String at) {
        LocalDateTime atTime = parseAt(at);
        FlagHistoryDO row = findHistory(flagKey, atTime);

        Flag flag = Flag.fromDefinition(row.getFlagKey(), row.getDefinition());

        EvalContext ctx = toEvalContext(env, app, userinfo);
        boolean enabled = evaluator.isEnabled(flag, ctx);
        String reason = describe(flag, userinfo == null ? null : userinfo.getUserId(), enabled);

        return ExplainDTO.builder()
                .enabled(enabled)
                .reason(reason)
                .flagVersion(row.getVersion())
                .userContext(buildContext(env, app, userinfo, atTime))
                .build();
    }

    private Map<String, Object> buildContext(String env, String app, UserInfo userinfo, LocalDateTime atTime) {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("at", atTime.toString());
        ctx.put("env", env);
        ctx.put("app", app);
        if (userinfo != null) {
            ctx.put("userId", userinfo.getUserId());
            ctx.put("region", userinfo.getRegion());
            ctx.put("tenant", userinfo.getTenant());
            ctx.put("role", userinfo.getRole());
        }
        return ctx;
    }

    private EvalContext toEvalContext(String env, String app, UserInfo userinfo) {
        EvalContext.EvalContextBuilder b = EvalContext.builder()
                .env(env)
                .app(app);
        if (userinfo == null) {
            return b.build();
        }
        b.userId(userinfo.getUserId())
                .region(userinfo.getRegion())
                .tenant(userinfo.getTenant());
        if (!userinfo.getAttrs().isEmpty()) {
            b.userAttrs(userinfo.getAttrs());
        }
        return b.build();
    }

    private FlagHistoryDO findHistory(String flagKey, LocalDateTime at) {
        LambdaQueryWrapper<FlagHistoryDO> qw = new LambdaQueryWrapper<>();
        qw.eq(FlagHistoryDO::getFlagKey, flagKey)
                .le(FlagHistoryDO::getUpdatedAt, at)
                .orderByDesc(FlagHistoryDO::getUpdatedAt)
                .last("LIMIT 1");
        FlagHistoryDO row = flagHistoryMapper.selectOne(qw);
        if (row == null) {
            throw new BizException(ErrorCode.FLAG_NOT_FOUND, flagKey + " at " + at);
        }
        return row;
    }

    private LocalDateTime parseAt(String at) {
        if (at == null) {
            return LocalDateTime.now();
        }
        try {
            return OffsetDateTime.parse(at).toLocalDateTime();
        } catch (Exception ignored) {
        }
        try {
            return LocalDateTime.parse(at);
        } catch (Exception ignored) {
        }
        throw new BizException(ErrorCode.INVALID_REQUEST);
    }

    private String describe(Flag flag, String userId, boolean enabled) {
        String type = flag.getType();
        if ("boolean".equals(type)) {
            return "boolean flag, value=" + flag.getValue() + " -> " + enabled;
        }
        if ("pct".equals(type)) {
            int bucket = bucket(flag, userId);
            return "pct=" + flag.getPct() + ", user bucket=" + bucket + " -> " + enabled;
        }
        if ("targeting".equals(type)) {
            return "targeting rules: " + flag.getRules().size() + " -> " + enabled;
        }
        return "unknown type=" + type;
    }

    private int bucket(Flag flag, String userId) {
        if (userId == null || userId.isEmpty()) {
            return -1;
        }
        String salt = flag.getSalt() == null ? "" : flag.getSalt();
        return Math.floorMod(HashUtil.hash32(userId + ":" + salt + ":" + flag.getKey()), 100);
    }
}