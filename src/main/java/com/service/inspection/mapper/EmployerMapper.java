package com.service.inspection.mapper;

import com.service.inspection.dto.employer.EmployerDto;
import com.service.inspection.dto.employer.GetEmployerDto;
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

    GetEmployerDto mapToDto(Employer employer);

    void mapToUpdateEmployer(@MappingTarget Employer toUpdate, EmployerDto source);
}
