package com.fms.sdk.model;

import lombok.Data;

/**
 * Rule: attr op value.
 *
 * @author matt.shi
 * @date 2026/07/02
 **/
@Data
public class Rule {
    private String attr;
    private String op;
    private Object value;
}