package com.service.inspection.configs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BucketName {

    USER_LOGO("images"),
    VERIFICATION_SCAN("images"),
    SIGNATURE("images"),
    LICENSE_SCAN("images"),
    SRO("images"),
    COMPANY_LOGO("images"),
    INSPECTION_MAIN_PHOTO("images"),
    CATEGORY_PHOTOS("images"),

    DEFAULT_IMAGE_BUCKET("images");

    private final String bucket;
}
