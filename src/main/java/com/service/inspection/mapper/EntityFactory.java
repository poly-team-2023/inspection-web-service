package com.service.inspection.mapper;

import java.util.List;

import com.service.inspection.entities.Company;
import com.service.inspection.entities.Employer;
import com.service.inspection.entities.Role;
import com.service.inspection.repositories.CompanyRepository;
import com.service.inspection.repositories.EmployerRepository;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EntityFactory {

    private final CompanyRepository companyRepository;
    private final EmployerRepository employerRepository;

    public Company createCompanyReferenceFromId(Long id) {
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

    @Named("userRole")
    List<Role> createDefaultRoleForUser(String dontNeed) {
        Role r = new Role();
        r.setId(1L);                 // для того, чтобы лишний раз не обращаться к бд
        return List.of(r);
    }
}
