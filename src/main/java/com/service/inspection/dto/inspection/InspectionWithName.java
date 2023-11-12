package com.service.inspection.dto.inspection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InspectionWithName extends InspectionWithIdOnly {

    @JsonProperty("inspectionName")
    private String name;
}
