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

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmployerService {

    private final EmployerRepository employerRepository;
    private final CompanyRepository companyRepository;
    private final StorageService storageService;
    private final EmployerMapper employerMapper;
    private final ServiceUtils serviceUtils;

    @Transactional
    public Identifiable addEmployer(long userId, Employer employer, long companyId, MultipartFile signature) {
        Company company = getCompanyIfExistForUser(userId, companyId);
        UUID signatureUuid = UUID.randomUUID();

        employer.setSignatureUuid(signatureUuid);
        employer.setSignatureName(signature.getOriginalFilename());
        employer.setCompany(company);

        employerRepository.save(employer);
        storageService.saveFile(BucketName.SIGNATURE, signatureUuid.toString(), signature);
        return employer;
    }

    @Transactional
    public void updateEmployer(long userId, long companyId, long employerId, EmployerDto dto) {
        Employer employer = serviceUtils.tryToFindByID(
                serviceUtils.getCompanyIfExistForUser(companyId, userId).getEmployers(),
                employerId
        );

        employerMapper.mapToUpdateEmployer(employer, dto);
        employerRepository.save(employer);
    }

    @Transactional
    public void deleteEmployer(long userId, long companyId, long employerId) {
        serviceUtils.tryToFindByID(
                serviceUtils.getCompanyIfExistForUser(companyId, userId).getEmployers(), employerId);
        employerRepository.deleteById(employerId); // TODO : add deletePic
    }

    public StorageService.BytesWithContentType getSignature(long userId, long companyId, long employerId) {
        Employer employer = serviceUtils.tryToFindByID(
                serviceUtils.getCompanyIfExistForUser(companyId, userId).getEmployers(), employerId);
        if (employer.getSignatureUuid() == null) {
            return null;
        }
        return storageService.getFile(BucketName.SIGNATURE, employer.getSignatureUuid().toString());
    }

    private Company getCompanyIfExistForUser(Long companyId, Long userId) {
        return companyRepository.findByUserIdAndId(userId, companyId).orElseThrow(() ->
                new EntityNotFoundException(String.format("No such inspection with id %s for this user", companyId)));
    }
}
