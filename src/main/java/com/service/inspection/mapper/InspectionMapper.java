package com.service.inspection.mapper;

import com.service.inspection.dto.inspection.InspectionWithName;
import com.service.inspection.entities.Inspection;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"
)
public interface InspectionMapper {
    InspectionWithName mapToInspectionWithName(Inspection inspection);
}
