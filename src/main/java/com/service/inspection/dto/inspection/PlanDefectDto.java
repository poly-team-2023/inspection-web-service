package com.service.inspection.dto.inspection;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class PlanDefectDto {

    private UUID typeUuid;
    private List<CoordsDto> coordsDtoList;
    private Boolean isClosed = Boolean.FALSE;

    private Instant lastUpdateDate = Instant.now();


    @Data
    private class CoordsDto {
        private Integer position;
        private Double x;
        private Double y;
    }
}
