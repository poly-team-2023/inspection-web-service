package com.service.inspection.service;

import com.service.inspection.configs.BucketName;
import com.service.inspection.dto.employer.EmployerDto;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.Employer;
import com.service.inspection.entities.User;
import com.service.inspection.mapper.EmployerMapper;
import com.service.inspection.repositories.EmployerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EmployerService {

     private final EmployerRepository employerRepository;
     private final StorageService storageService;
     private final EmployerMapper employerMapper;

     @Transactional
     public void addEmployer(User user, Employer employer, Company company, MultipartFile signature) {
         checkUser(company, user);
         UUID signatureUuid = UUID.randomUUID();

         storageService.saveFile(BucketName.SIGNATURE, signatureUuid.toString(), signature);
         employer.setSignatureUuid(signatureUuid);
         employer.setSignatureName(signature.getOriginalFilename());
         employer.setCompany(company);
         employerRepository.save(employer);
     }

     public void updateEmployer(User user, Company company, long id, EmployerDto dto) {
         checkUser(company, user);
         Employer employer = get(id);
         employerMapper.mapToUpdateEmployer(employer, dto);
         employerRepository.save(employer);
     }

     public void deleteEmployer(User user, Company company, long id) {
         checkUser(company, user);
         employerRepository.deleteById(id); // TODO : add deletePic
     }

     public Employer get(long id) {
         return employerRepository.findById(id)
                 .orElseThrow(() -> new NoSuchElementException("Employer with id " + id + " not found"));
     }

    private void checkUser(Company company, User user) {
        if (!company.getUser().equals(user)) {
            throw new RuntimeException("No access");
        }
    }
}
