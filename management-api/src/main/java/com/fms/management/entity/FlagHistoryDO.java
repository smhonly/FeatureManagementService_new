package com.fms.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fms.management.handler.JsonNodeTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("flag_history")
public class FlagHistoryDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String flagKey;

    private String env;

    private String app;

    private Long version;

    private String flagType;

    private String state;

    @TableField(typeHandler = JsonNodeTypeHandler.class)
    private JsonNode definition;

    private LocalDateTime updatedAt;

    private String updatedBy;

    private LocalDateTime createdAt;
}