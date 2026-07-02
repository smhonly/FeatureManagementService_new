package com.fms.management.service;

import com.fms.management.dto.response.ExplainDTO;
import com.fms.sdk.UserInfo;

public interface ExplainService {

    ExplainDTO explain(String flagKey, String env, String app, UserInfo userinfo, String at);
}