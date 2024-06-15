package com.service.inspection.dto.inspection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class PlanDefectDto {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UUID typeUuid;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private TypeDefectDto typeDefectDto;

    private List<CoordsDto> coordsDtoList;
    private Boolean isClosed = Boolean.FALSE;
    private Instant lastUpdateDate = Instant.now();


    @Data
    private static class CoordsDto {
        private Integer position;
        private Double x;
        private Double y;
    }
}
