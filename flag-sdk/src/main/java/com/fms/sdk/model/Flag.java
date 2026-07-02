package com.fms.sdk.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * flag definition
 *
 * @author matt.shi
 * @date 2026/07/02
 **/
@Data
public class Flag {
    private String key;
    private JsonNode definition; //raw definition;
    private String type;          //boolean / pct / targeting
    private Boolean value;        //boolean flag true/false
    private Integer pct;
    private String salt;
    private List<Rule> rules = new ArrayList<>();

    public static Flag fromDefinition(String key, JsonNode def) {
        Flag f = new Flag();
        f.setKey(key);
        f.setDefinition(def);
        if (def != null) {
            f.setType(def.path("type").asText());
            if (def.has("value")) {
                f.setValue(def.get("value").asBoolean());
            }
            if (def.has("pct")) {
                f.setPct(def.get("pct").asInt());
            }
            f.setSalt(def.path("salt").asText());
            JsonNode rules = def.get("rules");
            if (rules != null && rules.isArray()) {
                List<Rule> list = new ArrayList<>();
                for (JsonNode item : rules) {
                    Rule rule = new Rule();
                    rule.setAttr(item.path("attr").asText());
                    rule.setOp(item.path("op").asText());
                    rule.setValue(item.get("value"));
                    list.add(rule);
                }
                f.setRules(list);
            }
        }
        return f;
    }
}