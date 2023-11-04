package com.service.inspection.entities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BuildingType {
    CULTURE("Здание культурного наследие"), NOT_CULTURE("Здание");

    private final String type;
}
