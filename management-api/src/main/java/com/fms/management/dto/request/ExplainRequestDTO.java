package com.fms.management.dto.request;

import com.fms.sdk.UserInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExplainRequestDTO {

    @NotBlank
    private String flag;

    @NotBlank
    private String env;

    @NotBlank
    private String app;

    private UserInfo userinfo;

    private String at;
}