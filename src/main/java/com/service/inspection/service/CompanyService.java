package com.service.inspection.service;

import com.service.inspection.configs.BucketName;
import com.service.inspection.dto.company.CompanyDto;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.User;
import com.service.inspection.mapper.CompanyMapper;
import com.service.inspection.repositories.CompanyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CompanyService {

    private final StorageService storageService;
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
        companyRepository.delete(company); // TODO : add deletePic
    }

    @Transactional
    public void addSro(User user, long id, MultipartFile sro) {
        Company company = get(id);
        checkUser(company, user);

        UUID sroUuid = UUID.randomUUID();
        storageService.saveFile(BucketName.SRO, sroUuid.toString(), sro);
        company.setSroScanName(sro.getOriginalFilename());
        company.setSroScanUuid(sroUuid);
        companyRepository.save(company);
    }

    public void deleteSro(User user, long id) {
        Company company = get(id);
        checkUser(company, user);
        company.setSroScanUuid(null);
        company.setSroScanName(null);
        companyRepository.save(company); // TODO : add deletePic
    }

    @Transactional
    public void addLogo(User user, long id, MultipartFile logo) {
        Company company = get(id);
        checkUser(company, user);

        UUID logoUuid = UUID.randomUUID();
        storageService.saveFile(BucketName.COMPANY_LOGO, logoUuid.toString(), logo);
        company.setLogoName(logo.getOriginalFilename());
        company.setLogoUuid(logoUuid);
        companyRepository.save(company);
    }

    private void checkUser(Company company, User user) {
        if (!company.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No access");
        }
    }
}
