package com.service.inspection.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Data
public class PhotoDefectsDto {

    @JsonProperty(value = "defects")
    private Set<DefectDto> defectsDto;

    @Data
    public static class DefectDto {

        @JsonProperty("class_name")
        private String defectName;

        private Double confidence;

        @JsonProperty("coords_x")
        private List<Integer> coordsX;

        @JsonProperty("coords_y")
        private List<Integer> coordsY;
    }
}
