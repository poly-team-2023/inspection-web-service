package com.service.inspection.advice;

import lombok.Data;

import java.util.List;

@Data
public class ValidationErrorResponse {

    private final List<Violation> violationList;

    @Data
    public static class Violation {
        private final String field;
        private final String message;
    }
}
