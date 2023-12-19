package com.service.inspection.service;

import java.util.UUID;

import com.service.inspection.configs.BucketName;
import com.service.inspection.dto.license.LicenseDto;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.FileScan;
import com.service.inspection.entities.Identifiable;
import com.service.inspection.entities.License;
import com.service.inspection.mapper.LicenseMapper;
import com.service.inspection.repositories.CompanyRepository;
import com.service.inspection.repositories.FileScanRepository;
import com.service.inspection.repositories.LicenseRepository;
import com.service.inspection.utils.ServiceUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LicenseService {

    private final StorageService storageService;
    private final LicenseRepository licenseRepository;
    private final LicenseMapper licenseMapper;
    private final ServiceUtils serviceUtils;
    private final FileScanRepository fileScanRepository;
    private final CompanyRepository companyRepository;


    public Identifiable addLicense(long userId, long companyId, License license) {
        Company company = serviceUtils.getCompanyIfExistForUser(userId, companyId);

        license.setCompany(company);
        licenseRepository.save(license);
        return license;
    }

    @Transactional
    public Identifiable addLicenseScan(long userId, long companyId, long licenseId, int scanNumber, MultipartFile scan) {
        License license = getLicense(companyId, userId, licenseId);
        UUID scanUuid = UUID.randomUUID();

        FileScan fileScan = new FileScan();
        fileScan.setName(scan.getOriginalFilename());
        fileScan.setScanNumber(scanNumber);
        fileScan.setFileUuid(scanUuid);
        fileScan.setLicense(license);
        fileScanRepository.save(fileScan);

        storageService.saveFile(BucketName.LICENSE_SCAN, scanUuid.toString(), scan);
        return fileScan;
    }

    public void updateLicenseScan(long userId, long companyId, long licenseId, long sroId, int scanNumber) {
        License license = getLicense(companyId, userId, licenseId);
        FileScan scan = serviceUtils.tryToFindByID(license.getFiles(), sroId);
        scan.setScanNumber(scanNumber);
        fileScanRepository.save(scan);
    }

    public void deleteLicenseScan(long userId, long companyId, long licenseId, long sroId) {
        License license = getLicense(companyId, userId, licenseId);
        FileScan scan = serviceUtils.tryToFindByID(license.getFiles(), sroId);
        fileScanRepository.delete(scan);
    }

    public void deleteAllLicenseScan(long userId, long companyId, long licenseId) {
        License license = getLicense(companyId, userId, licenseId);
        fileScanRepository.deleteAll(license.getFiles());
    }

    public void updateLicense(long userId, long companyId, long licenseId, LicenseDto dto) {
        License license = getLicense(companyId, userId, licenseId);

        licenseMapper.mapToUpdateLicense(license, dto);
        licenseRepository.save(license);
    }

    public void deleteLicense(long userId, long companyId, long licenseId) {
        Company company = serviceUtils.getCompanyIfExistForUser(userId, companyId);
        License license = serviceUtils.tryToFindByID(company.getLicenses(), licenseId);
        company.getLicenses().remove(license);
        companyRepository.save(company);
        licenseRepository.delete(license);
    }

    public StorageService.BytesWithContentType getLicenseScan(Long companyId, Long userId,
                                                              Long licenseId, Long sroId) {
        FileScan scan = serviceUtils.tryToFindByID(getLicense(companyId, userId, licenseId).getFiles(), sroId);
        return storageService.getFile(BucketName.LICENSE_SCAN, scan.getFileUuid().toString());
    }

    private License getLicense(Long companyId, Long userId, Long licenseId) {
        return serviceUtils.tryToFindByID(
                serviceUtils.getCompanyIfExistForUser(userId, companyId).getLicenses(), licenseId);
    }
}
