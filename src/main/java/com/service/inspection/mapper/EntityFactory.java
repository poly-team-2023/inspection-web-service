package com.service.inspection.mapper;

import com.service.inspection.entities.*;
import com.service.inspection.repositories.CategoryRepository;
import com.service.inspection.repositories.CompanyRepository;
import com.service.inspection.repositories.EmployerRepository;
import com.service.inspection.repositories.InspectionRepository;
import com.service.inspection.repositories.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EntityFactory {

    private final CompanyRepository companyRepository;
    private final EmployerRepository employerRepository;
    private final PlanRepository planRepository;
    private final CategoryRepository categoryRepository;
    private final InspectionRepository inspectionRepository;

    Inspection createInspectionReferenceFromId(Long id) {
        if (id == null) {
            return null;
        }
        return inspectionRepository.getReferenceById(id);
    }

    Company createCompanyReferenceFromId(Long id) {
        if (id == null) {
            return null;
        }
        return companyRepository.getReferenceById(id);
    }

    // TODO проверка на то что пользователи и компания существует
    Employer createEmployerReferenceFromId(Long id) {
        if (id == null) {
            return null;
        }
        return employerRepository.getReferenceById(id);
    }

    Category createCategoryReference(Long id) {
        if (id == null) {
            return null;
        }
        return categoryRepository.getReferenceById(id);
    }


    Plan createPlanReferenceFromId(Long id) {
        if (id == null) {
            return null;
        }
        return planRepository.getReferenceById(id);
    }

    @Named("userRole")
    List<Role> createDefaultRoleForUser(String dontNeed) {
        Role r = new Role();
        r.setId(1L);                 // для того, чтобы лишний раз не обращаться к бд
        return List.of(r);
    }
}
