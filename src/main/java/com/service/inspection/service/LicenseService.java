package com.service.inspection.service;

import com.service.inspection.configs.BucketName;
import com.service.inspection.dto.license.LicenseDto;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.License;
import com.service.inspection.entities.User;
import com.service.inspection.mapper.LicenseMapper;
import com.service.inspection.repositories.LicenseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LicenseService {

    private final StorageService storageService;
    private final LicenseRepository licenseRepository;
    private final LicenseMapper licenseMapper;

    public License get(long id) {
        return licenseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Equipment with id " + id + " not found"));
    }

    public void addLicense(User user, Company company, License license) {
        checkUser(company, user);
        license.setCompany(company);
        licenseRepository.save(license);
    }

    @Transactional
    public void addLicensePicture(User user, Company company, long id, MultipartFile scan) {
        checkUser(company, user);
        UUID scanUuid = UUID.randomUUID();

        License license = get(id);
        license.setUuid(scanUuid);
        licenseRepository.save(license);

        storageService.saveFile(BucketName.LICENSE_SCAN, scanUuid.toString(), scan);
    }

    public void updateLicense(User user, Company company, long id, LicenseDto dto) {
        checkUser(company, user);
        License license = get(id);
        licenseMapper.mapToUpdateLicense(license, dto);
        licenseRepository.save(license);
    }

    public void deleteLicense(User user, Company company, long id) {
        checkUser(company, user);
        licenseRepository.deleteById(id);
    }

    private void checkUser(Company company, User user) {
        if (!company.getUser().equals(user)) {
            throw new RuntimeException("No access");
        }
    }
}
