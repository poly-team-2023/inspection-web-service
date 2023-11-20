package com.service.inspection.mapper;

import com.service.inspection.dto.company.CompanyDto;
import com.service.inspection.dto.company.GetCompanyDto;
import com.service.inspection.entities.Company;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring",
        uses = {EmployerMapper.class, LicenseMapper.class}
)
public interface CompanyMapper {

    void mapToUpdateCompany(@MappingTarget Company toUpdate, CompanyDto source);

    GetCompanyDto mapToDto(Company company);
}
