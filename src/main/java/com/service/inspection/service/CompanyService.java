package com.service.inspection.service;

import java.util.NoSuchElementException;
import java.util.UUID;

import com.service.inspection.configs.BucketName;
import com.service.inspection.dto.company.CompanyDto;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.Identifiable;
import com.service.inspection.entities.User;
import com.service.inspection.mapper.CompanyMapper;
import com.service.inspection.repositories.CompanyRepository;
import com.service.inspection.utils.ServiceUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import java.util.List;
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

    public List<Company> getCompanies(long id) {
        return companyRepository.findAllByUserId(id);
    }

    public Identifiable createCompany(User user) {
        Company company = new Company();
        company.setUser(user);
        companyRepository.save(company);
        return company;
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

    public StorageService.BytesWithContentType getLogo(Long companyId, Long userId) {
        Company company = serviceUtils.getCompanyIfExistForUser(companyId, userId);
        if (company.getLogoUuid() == null) {
            return null;
        }
        return storageService.getFile(BucketName.COMPANY_LOGO, company.getLogoUuid().toString());
    }

    public StorageService.BytesWithContentType getSroScan(Long companyId, Long userId) {
        Company company = serviceUtils.getCompanyIfExistForUser(companyId, userId);
        if (company.getSroScanUuid() == null) {
            return null;
        }
        return storageService.getFile(BucketName.SRO, company.getSroScanUuid().toString());
    }
}
