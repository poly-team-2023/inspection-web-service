package com.service.inspection.mapper;

import com.service.inspection.dto.inspection.GetInspectionDto;
import com.service.inspection.dto.inspection.InspectionDto;
import com.service.inspection.dto.inspection.InspectionWithName;
import com.service.inspection.entities.Inspection;

import com.service.inspection.entities.enums.BuildingType;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Objects;

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
    @Mapping(source = "isCulture", target = "buildingType")
    void mapToInspection(@MappingTarget Inspection inspection, InspectionDto inspectionDto);


    @Mapping(source = "buildingType", target = "isCulture")
    GetInspectionDto mapToGetInspectionDto(Inspection inspection);

    default Boolean toIsCultureBuildingType(BuildingType buildingType) {
        return Objects.equals(buildingType, BuildingType.CULTURE);
    }

    default BuildingType toBuildingType(Boolean isCulture) {
        if (Boolean.TRUE.equals(isCulture)) {
            return BuildingType.CULTURE;
        }
        return BuildingType.NOT_CULTURE;
    }
}
