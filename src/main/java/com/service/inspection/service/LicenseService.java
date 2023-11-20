package com.service.inspection.service;

import com.service.inspection.configs.BucketName;
import com.service.inspection.dto.license.LicenseDto;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.License;
import com.service.inspection.mapper.LicenseMapper;
import com.service.inspection.repositories.LicenseRepository;
import com.service.inspection.utils.ServiceUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@AllArgsConstructor
public class LicenseService {

    private final StorageService storageService;
    private final LicenseRepository licenseRepository;
    private final LicenseMapper licenseMapper;
    private final ServiceUtils serviceUtils;


    public void addLicense(long userId, long companyId, License license) {
        Company company = serviceUtils.getCompanyIfExistForUser(companyId, userId);

        license.setCompany(company);
        licenseRepository.save(license);
    }

    @Transactional
    public void addLicensePicture(long userId, long companyId, long licenseId, MultipartFile scan) {
        License license = serviceUtils.tryToFindByID(
                serviceUtils.getCompanyIfExistForUser(companyId, userId).getLicenses(), licenseId);
        UUID scanUuid = UUID.randomUUID();

        license.setUuid(scanUuid);
        licenseRepository.save(license);

        storageService.saveFile(BucketName.LICENSE_SCAN, scanUuid.toString(), scan);
    }

    public void updateLicense(long userId, long companyId, long licenseId, LicenseDto dto) {
        License license = serviceUtils.tryToFindByID(
                serviceUtils.getCompanyIfExistForUser(companyId, userId).getLicenses(), licenseId);

        licenseMapper.mapToUpdateLicense(license, dto);
        licenseRepository.save(license);
    }

    public void deleteLicense(long userId, long companyId, long licenseId) {
        serviceUtils.tryToFindByID(
                serviceUtils.getCompanyIfExistForUser(companyId, userId).getEmployers(), licenseId);
        licenseRepository.deleteById(licenseId);
    }
}
