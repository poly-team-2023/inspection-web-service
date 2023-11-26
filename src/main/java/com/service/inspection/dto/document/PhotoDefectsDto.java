package com.service.inspection.dto.document;

import lombok.Data;

import java.util.Set;

@Data
public class PhotoDefectsDto {

    private Set<DefectDto> defectsDto;

    @Data
    public static class DefectDto {
        private String defectName;
    }
}
