package com.service.inspection.mapper;

import com.service.inspection.dto.employer.EmployerDto;
import com.service.inspection.entities.Employer;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"
)
public interface EmployerMapper {

    Employer mapToEmployer(String name, String positionName);

    EmployerDto mapToEmployerDto(String name, String positionName);

    void mapToUpdateEmployer(@MappingTarget Employer toUpdate, EmployerDto source);
}
