package com.fms.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fms.management.handler.JsonNodeTypeHandler;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("flags")
public class FlagDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String flagKey;

    private String env;

    private String app;

    // boolean or pct or targeting
    private String flagType;

    // active or archived
    private String state;

    // JSON body, never null
    @TableField(typeHandler = JsonNodeTypeHandler.class)
    private JsonNode definition;

    //Optimistic Lock
    @Version
    private Long version;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;
}
