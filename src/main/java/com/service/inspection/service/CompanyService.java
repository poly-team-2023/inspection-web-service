package com.service.inspection.service;

import com.service.inspection.configs.BucketName;
import com.service.inspection.dto.company.CompanyDto;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.User;
import com.service.inspection.mapper.CompanyMapper;
import com.service.inspection.repositories.CompanyRepository;
import com.service.inspection.utils.ServiceUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
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
    private final ServiceUtils serviceUtils;

    public Company get(long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Company with id " + id + " not found"));
    }

    public void createCompany(User user) {
        Company company = new Company();
        company.setUser(user);
        companyRepository.save(company);
    }


    public void updateCompany(long userId, long companyId, CompanyDto dto) {
        Company company = serviceUtils.getCompanyIfExistForUser(userId, companyId);

        companyMapper.mapToUpdateCompany(company, dto);
        companyRepository.save(company);
    }

    public void deleteCompany(long userId, long companyId) {
        Company company = serviceUtils.getCompanyIfExistForUser(userId, companyId);

        companyRepository.delete(company); // TODO : add deletePic
    }

    @Transactional
    public void addSro(long userId, long companyId, MultipartFile sro) {
        Company company = serviceUtils.getCompanyIfExistForUser(userId, companyId);

        UUID sroUuid = UUID.randomUUID();
        storageService.saveFile(BucketName.SRO, sroUuid.toString(), sro);
        company.setSroScanName(sro.getOriginalFilename());
        company.setSroScanUuid(sroUuid);

        companyRepository.save(company);
    }

    public void deleteSro(long userId, long companyId) {
        Company company = serviceUtils.getCompanyIfExistForUser(userId, companyId);

        company.setSroScanUuid(null);
        company.setSroScanName(null);
        companyRepository.save(company); // TODO : add deletePic
    }

    @Transactional
    public void addLogo(long userId, long companyId, MultipartFile logo) {
        Company company = serviceUtils.getCompanyIfExistForUser(userId, companyId);

        UUID logoUuid = UUID.randomUUID();
        company.setLogoName(logo.getOriginalFilename());
        company.setLogoUuid(logoUuid);
        companyRepository.save(company);

        storageService.saveFile(BucketName.COMPANY_LOGO, logoUuid.toString(), logo);
    }
}
