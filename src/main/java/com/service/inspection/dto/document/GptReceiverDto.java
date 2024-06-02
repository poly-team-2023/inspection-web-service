package com.service.inspection.dto.document;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GptReceiverDto {

    @ToString.Include
    private GptBuildingInfo building;

    @Data
    @ToString
    public static class GptReceiverCategoryDto {
        @ToString.Include
        private Long id;
        @ToString.Include
        private String name;
        @ToString.Include
        private String recommendation;
        @ToString.Include
        private String estimation;
        @ToString.Include
        private List<GptReceiverDefectsDto> defects;
    }

    @Data
    @ToString
    public static class GptReceiverDefectsDto {
        @ToString.Include
        private String name;

        @ToString.Include
        private String recommendation;

        @ToString.Include
        private String estimation;
    }

    @Data
    @ToString
    public static class GptBuildingInfo {

        @ToString.Include
        private List<GptReceiverCategoryDto> categories;

        @ToString.Include
        private String recommendation;

        @ToString.Include
        private String estimation;
    }
}
