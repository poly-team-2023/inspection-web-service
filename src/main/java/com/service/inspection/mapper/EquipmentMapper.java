package com.service.inspection.mapper;

import com.service.inspection.dto.equipment.EquipmentDto;
import com.service.inspection.dto.equipment.GetEquipmentDto;
import com.service.inspection.entities.Equipment;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"
)
public interface EquipmentMapper {

    Equipment mapToEquipment(EquipmentDto dto);

    GetEquipmentDto mapToDto(Equipment equipment);

    void mapToUpdateEquipment(@MappingTarget Equipment toUpdate, EquipmentDto source);
}
