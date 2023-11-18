package com.service.inspection.mapper;

import com.service.inspection.dto.inspection.InspectionDto;
import com.service.inspection.dto.inspection.InspectionWithName;
import com.service.inspection.entities.Inspection;
import org.mapstruct.*;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring",
        uses = {EntityFactory.class}
)
public interface InspectionMapper {
    InspectionWithName mapToInspectionWithName(Inspection inspection);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", source = "companyId", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(target = "employer", source = "employerId", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void mapToInspection(@MappingTarget Inspection inspection, InspectionDto inspectionDto);
}
