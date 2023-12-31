package com.service.inspection.service;

import java.util.UUID;

import com.service.inspection.configs.BucketName;
import com.service.inspection.dto.employer.EmployerDto;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.Employer;
import com.service.inspection.entities.Identifiable;
import com.service.inspection.mapper.EmployerMapper;
import com.service.inspection.repositories.CompanyRepository;
import com.service.inspection.repositories.EmployerRepository;
import com.service.inspection.utils.ServiceUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmployerService {

    private final EmployerRepository employerRepository;
    private final StorageService storageService;
    private final EmployerMapper employerMapper;
    private final ServiceUtils serviceUtils;
    private final CompanyRepository companyRepository;

    @Transactional
    public Identifiable addEmployer(long userId, Employer employer, long companyId, MultipartFile signature) {
        Company company = serviceUtils.getCompanyIfExistForUser(userId, companyId);
        UUID uuid = setSignature(employer, signature);
        employer.setCompany(company);

        employerRepository.save(employer);
        storageService.saveFile(BucketName.SIGNATURE, uuid.toString(), signature);
        return employer;
    }

    @Transactional
    public void updateEmployer(long userId, long companyId, long employerId,
                               EmployerDto dto, MultipartFile signature) {
        Employer employer = serviceUtils.tryToFindByID(
                serviceUtils.getCompanyIfExistForUser(userId, companyId).getEmployers(), employerId);
        employerMapper.mapToUpdateEmployer(employer, dto);
        UUID uuid = signature != null ? setSignature(employer, signature) : null;

        employerRepository.save(employer);
        if (uuid != null) {
            storageService.saveFile(BucketName.SIGNATURE, uuid.toString(), signature);
        }
    }

    @Transactional
    public void deleteEmployer(long userId, long companyId, long employerId) {
        Company company = serviceUtils.getCompanyIfExistForUser(userId, companyId);
        Employer employer = serviceUtils.tryToFindByID(company.getEmployers(), employerId);
        company.getEmployers().remove(employer);
        companyRepository.save(company);
        employerRepository.deleteById(employerId);
    }

    public StorageService.BytesWithContentType getSignature(long userId, long companyId, long employerId) {
        Employer employer = serviceUtils.tryToFindByID(
                serviceUtils.getCompanyIfExistForUser(userId, companyId).getEmployers(), employerId);
        if (employer.getSignatureUuid() == null) {
            return null;
        }
        return storageService.getFile(BucketName.SIGNATURE, employer.getSignatureUuid().toString());
    }

    private UUID setSignature(Employer employer, MultipartFile signature) {
        UUID signatureUuid = UUID.randomUUID();
        employer.setSignatureUuid(signatureUuid);
        employer.setSignatureName(signature.getOriginalFilename());
        return signatureUuid;
    }
}
