package com.service.inspection.service;

import java.util.Set;
import java.util.UUID;

import com.service.inspection.configs.BucketName;
import com.service.inspection.dto.company.CompanyDto;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.FileScan;
import com.service.inspection.entities.Identifiable;
import com.service.inspection.entities.User;
import com.service.inspection.mapper.CompanyMapper;
import com.service.inspection.repositories.CompanyRepository;
import com.service.inspection.repositories.FileScanRepository;
import com.service.inspection.utils.ServiceUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import java.util.List;

@Service
@AllArgsConstructor
public class CompanyService {

    private final StorageService storageService;
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final ServiceUtils serviceUtils;
    private final FileScanRepository fileScanRepository;

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
    public Identifiable addSro(long userId, long companyId, int scanNumber, MultipartFile sro) {
        Company company = serviceUtils.getCompanyIfExistForUser(userId, companyId);

        UUID sroUuid = UUID.randomUUID();

        FileScan fileScan = new FileScan();
        fileScan.setName(sro.getOriginalFilename());
        fileScan.setScanNumber(scanNumber);
        fileScan.setFileUuid(sroUuid);
        fileScan.setCompany(company);

        company.addSro(fileScan);
        fileScanRepository.save(fileScan);
        storageService.saveFile(BucketName.SRO, sroUuid.toString(), sro);
        return fileScan;
    }

    public void deleteSro(long userId, long companyId, long sroId) {
        Company company = serviceUtils.getCompanyIfExistForUser(userId, companyId);
        FileScan fileScan = serviceUtils.tryToFindByID(company.getFilesSro(), sroId);
        company.getFilesSro().remove(fileScan);
        companyRepository.save(company);
        fileScanRepository.deleteById(sroId);
    }

    public void updateSro(long userId, long companyId, long sroId, int scanNumber) {
        FileScan sro = serviceUtils.tryToFindByID(
                serviceUtils.getCompanyIfExistForUser(userId, companyId).getFilesSro(), sroId);
        sro.setScanNumber(scanNumber);
        fileScanRepository.save(sro);
    }

    public void deleteAllSro(long userId, long companyId) {
        Company company = serviceUtils.getCompanyIfExistForUser(userId, companyId);
        List<FileScan> fileScanSet = company.getFilesSro();
        company.getFilesSro().clear();
        companyRepository.save(company);
        fileScanRepository.deleteAll(fileScanSet);
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

    public void deleteLogo(long userId, long companyId) {
        Company company = serviceUtils.getCompanyIfExistForUser(userId, companyId);
        company.setLogoUuid(null);
        company.setLogoName(null);
        companyRepository.save(company);
    }

    public StorageService.BytesWithContentType getLogo(Long companyId, Long userId) {
        Company company = serviceUtils.getCompanyIfExistForUser(userId, companyId);
        if (company.getLogoUuid() == null) {
            return null;
        }
        return storageService.getFile(BucketName.COMPANY_LOGO, company.getLogoUuid().toString());
    }

    public StorageService.BytesWithContentType getSroScan(Long companyId, Long userId, Long sroId) {
        FileScan sro = serviceUtils.tryToFindByID(
                serviceUtils.getCompanyIfExistForUser(userId, companyId).getFilesSro(), sroId);
        return storageService.getFile(BucketName.SRO, sro.getFileUuid().toString());
    }
}
