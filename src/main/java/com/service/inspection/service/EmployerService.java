package com.service.inspection.service;

import com.service.inspection.configs.BucketName;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.Employer;
import com.service.inspection.entities.User;
import com.service.inspection.repositories.EmployerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EmployerService {

     private final EmployerRepository employerRepository;
     private final StorageService storageService;

     @Transactional
     public void addEmployer(User user, Employer employer, Company company, MultipartFile signature) {
         checkUser(company, user);
         UUID signatureUuid = UUID.randomUUID();

         employer.setSignatureUuid(signatureUuid);
         employer.setCompany(company);
         storageService.saveFile(BucketName.SIGNATURE, signatureUuid.toString(), signature);
         employerRepository.save(employer);
     }

     public void deleteEmployer(User user, Company company, long id) {
         checkUser(company, user);
         employerRepository.deleteById(id);
     }

     public List<Employer> getEmployersByCompany(User user, Company company) {
         checkUser(company, user);
         return (List<Employer>) employerRepository.findEmployersByCompanyId(company.getId());
     }

    private void checkUser(Company company, User user) {
        if (!company.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No access");
        }
    }
}
