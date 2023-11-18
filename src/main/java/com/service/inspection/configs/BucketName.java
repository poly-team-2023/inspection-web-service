package com.service.inspection.configs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BucketName {
    USER_LOGO("images"),
    VERIFICATION_SCAN("verification"),
    SIGNATURE("signature"),
    LICENSE_SCAN("license"),
    SRO("sro"),
    COMPANY_LOGO("images"),
    INSPECTION_MAIN_PHOTO("main-photo"),
    CATEGORY_PHOTOS("photos");

    private final String bucket;
}
