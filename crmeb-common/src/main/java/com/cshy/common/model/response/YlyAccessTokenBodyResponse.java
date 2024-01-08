package com.cshy.common.model.response;

import lombok.Data;

@Data
public class YlyAccessTokenBodyResponse {
    private String access_token;
    private String refresh_token;
    private String machine_code;
    private Integer expires_in;
    private String scope;
}
