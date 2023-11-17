package com.service.inspection.mapper;

import com.service.inspection.dto.IdentifiableDto;
import com.service.inspection.dto.inspection.InspectionDto;
import com.service.inspection.dto.inspection.InspectionWithName;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.Employer;
import com.service.inspection.entities.Identifiable;
import com.service.inspection.entities.Inspection;
import com.service.inspection.repositories.CompanyRepository;
import com.service.inspection.repositories.EmployerRepository;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"
)
public abstract class InspectionMapper {

    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    EmployerRepository employerRepository;

    public abstract InspectionWithName mapToInspectionWithName(Inspection inspection);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", source = "companyId", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(target = "employer", source = "employerId", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    public abstract void mapToInspection(@MappingTarget Inspection inspection, InspectionDto inspectionDto);

    Company mapToCompany(Long id) {
        if (id == null) {
            return null;
        }
        return companyRepository.getReferenceById(id);
    }
                                                    // TODO проверка на то что пользователи и компания существует
    Employer mapToEmployer(Long id) {
        if (id == null) {
            return null;
        }
        return employerRepository.getReferenceById(id);
    }
}
