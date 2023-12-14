package com.cshy.common.model.response;

import lombok.Data;

@Data
public class YlyAccessTokenResponse {
    private String error;
    private String error_description;
    private YlyAccessTokenBodyResponse body;
}
