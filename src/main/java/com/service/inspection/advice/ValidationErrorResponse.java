package com.service.inspection.advice;

import java.util.List;

import lombok.Data;

@Data
public class ValidationErrorResponse {

    private final List<Violation> violationList;

    @Data
    public static class Violation {
        private final String field;
        private final String message;
    }
}
