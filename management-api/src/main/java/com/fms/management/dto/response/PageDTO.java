package com.fms.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO<T> {
    private long total;
    private long current;
    private long size;
    private List<T> records;
}
