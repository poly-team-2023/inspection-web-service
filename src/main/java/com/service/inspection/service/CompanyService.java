package com.service.inspection.service;

import com.service.inspection.dto.company.CompanyDto;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.Employer;
import com.service.inspection.entities.User;
import com.service.inspection.mapper.CompanyMapper;
import com.service.inspection.repositories.CompanyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public Company get(long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Company with id " + id + " not found"));
    }

    public void createCompany(User user) {
        Company company = new Company();
        company.setUser(user);
        companyRepository.save(company);
    }

    public void updateCompany(User user, long id, CompanyDto dto) {
        Company company = get(id);
        checkUser(company, user);

        companyMapper.mapToUpdateCompany(company, dto);
        companyRepository.save(company);
    }

    public void deleteCompany(User user, long id) {
        Company company = get(id);
        checkUser(company, user);
        companyRepository.delete(company);
    }

    private void checkUser(Company company, User user) {
        if (!company.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No access");
        }
    }
}
