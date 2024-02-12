package com.service.inspection.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class CommonUtils {

    public String toHumanReadable(String s) {
        return StringUtils.capitalize(StringUtils.lowerCase(s));
    }
}
