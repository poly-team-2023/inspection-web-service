package com.service.inspection.dto.document;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class GptSenderDto {

    private List<GptCategoryDto> categories;

    @Data
    public static class GptCategoryDto {
        private Long id;
        private String name;
        private List<GptDefectDto> defects;
    }

    @Data
    @AllArgsConstructor
    public static class GptDefectDto {
        private String name;
        private Long count;
    }
}
