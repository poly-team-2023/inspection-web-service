package com.service.inspection.mapper;

import com.service.inspection.dto.inspection.InspectionWithTypeDefect;
import com.service.inspection.dto.inspection.TypeDefectDto;
import com.service.inspection.entities.DefectType;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring", uses = EntityFactory.class
)
public abstract class PlanDefectMapper {

    @Mapping(source = "dto", target = ".")
    @Mapping(source = "inspectionId", target = "inspection")
    @Mapping(target = "uuid", ignore = true)
    public abstract DefectType mapToNewDefectType(TypeDefectDto dto, Long inspectionId);
}
