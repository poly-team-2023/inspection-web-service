package com.service.inspection.configs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BucketName {
    USER_LOGO("mages"),
    VERIFICATION_SCAN("verification"),
    SIGNATURE("signature"),
    LICENSE_SCAN("license"),
    SRO("sro"),
    COMPANY_LOGO("images");

    private final String bucket;
}
