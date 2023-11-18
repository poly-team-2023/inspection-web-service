package com.service.inspection.configs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BucketName {
    USER_LOGO("user-logo"), INSPECTION_MAIN_PHOTO("main-photo"), CATEGORY_PHOTOS("photos");

    private final String bucket;
}
