package com.service.inspection.configs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BucketName {
    USER_LOGO("images"),
    VERIFICATION_SCAN("documents");

    private final String bucket;
}
