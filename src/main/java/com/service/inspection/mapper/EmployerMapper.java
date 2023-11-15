package com.service.inspection.mapper;

import com.service.inspection.dto.employer.EmployerDto;
import com.service.inspection.dto.employer.GetEmployerDto;
import com.service.inspection.entities.Employer;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"
)
public interface EmployerMapper {

    Employer mapToEmployer(EmployerDto dto);

    GetEmployerDto mapToDto(Employer employer);
}
