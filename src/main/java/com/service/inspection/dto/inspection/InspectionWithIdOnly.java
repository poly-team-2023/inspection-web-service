package com.service.inspection.dto.inspection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InspectionWithIdOnly {

    @JsonProperty("inspectionId")
    private Long id;
}
