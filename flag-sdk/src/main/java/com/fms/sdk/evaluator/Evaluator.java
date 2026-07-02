package com.fms.sdk.evaluator;

import com.fms.sdk.EvalContext;
import com.fms.sdk.utils.HashUtil;
import com.fms.sdk.model.Flag;
import com.fms.sdk.model.Rule;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Collection;
import java.util.List;

/**
 * Flag evaluation.
 *
 * @author matt.shi
 * @date 2026/07/02
 **/
public class Evaluator {

    public boolean isEnabled(Flag flag, EvalContext ctx) {
        if (flag == null || flag.getType() == null) {
            return false;
        }

        String type = flag.getType();
        if ("boolean".equals(type)) {
            return evalBoolean(flag);
        }
        if ("pct".equals(type)) {
            return evalPct(flag, ctx);
        }
        if ("targeting".equals(type)) {
            return evalTargeting(flag, ctx);
        }
        return false;
    }

    private boolean evalBoolean(Flag flag) {
        return BooleanUtils.isTrue(flag.getValue());
    }

    private boolean evalTargeting(Flag flag, EvalContext ctx) {
        if (flag.getRules().isEmpty()) {
            return false;
        }
        return allMatch(flag.getRules(), ctx);
    }

    private boolean evalPct(Flag flag, EvalContext ctx) {
        if (!flag.getRules().isEmpty() && !allMatch(flag.getRules(), ctx)) {
            return false;
        }

        Integer pct = flag.getPct();
        if (pct == null || pct <= 0) {
            return false;
        }
        if (pct >= 100) {
            return true;
        }

        String userId = ctx.getUserId();
        if (userId == null || userId.isEmpty()) {
            return false;
        }

        String salt = flag.getSalt();
        if (salt == null) {
            salt = "";
        }

        int bucket = Math.floorMod(HashUtil.hash32(userId + ":" + salt + ":" + flag.getKey()), 100);
        return bucket < pct;
    }

    private boolean allMatch(List<Rule> rules, EvalContext ctx) {
        for (int i = 0; i < rules.size(); i++) {
            if (!matchRule(rules.get(i), ctx)) {
                return false;
            }
        }
        return true;
    }

    private boolean matchRule(Rule r, EvalContext ctx) {
        Object actual = ctx.attr(r.getAttr());
        Object expected = r.getValue();
        String op = r.getOp();
        if (op == null) {
            op = "eq";
        }
        if ("in".equals(op)) {
            return contains(expected, actual);
        }
        if (actual == null || expected == null) {
            return actual == expected;
        }
        return String.valueOf(actual).equals(String.valueOf(expected));
    }

    private static boolean contains(Object container, Object value) {
        if (!(container instanceof Collection)) {
            return false;
        }
        String s = value == null ? null : String.valueOf(value);
        for (Object item : (Collection<?>) container) {
            if (s == null) {
                if (item == null) {
                    return true;
                }
            } else if (s.equals(String.valueOf(item))) {
                return true;
            }
        }
        return false;
    }
}