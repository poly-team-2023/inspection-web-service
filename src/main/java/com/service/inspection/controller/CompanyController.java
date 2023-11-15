package com.service.inspection.controller;

import com.service.inspection.dto.company.CompanyDto;
import com.service.inspection.dto.company.GetCompanyDto;
import com.service.inspection.dto.employer.EmployerDto;
import com.service.inspection.dto.license.LicenseDto;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.User;
import com.service.inspection.mapper.CompanyMapper;
import com.service.inspection.mapper.EmployerMapper;
import com.service.inspection.mapper.LicenseMapper;
import com.service.inspection.service.CompanyService;
import com.service.inspection.service.EmployerService;
import com.service.inspection.service.LicenseService;
import com.service.inspection.service.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@RestController
@RequestMapping( "/api/v1/company")
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final EmployerService employerService;
    private final LicenseService licenseService;
    private final CompanyMapper companyMapper;
    private final EmployerMapper employerMapper;
    private final LicenseMapper licenseMapper;

    @PostMapping("/create")
    @Operation(summary = "Создать компанию")
    public ResponseEntity<Void> createCompany(Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        companyService.createCompany(user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{comp_id}/update")
    @Operation(summary = "Обновление текстовых полей")
    public ResponseEntity<Void> updateCompany(@PathVariable("comp_id") long id,
                                              @RequestBody @Valid CompanyDto dto,
                                              Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        companyService.updateCompany(user, id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{comp_id}/delete")
    @Operation(summary = "Удаление компании")
    public ResponseEntity<Void> deleteCompany(@PathVariable("comp_id") long id,
                                              Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        companyService.deleteCompany(user, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{comp_id}")
    public ResponseEntity<GetCompanyDto> getCompany(@PathVariable("comp_id") long id,
                                                    Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        Company company = companyService.get(id);
        return ResponseEntity.ok(companyMapper.mapToDto(
                company,
                employerService.getEmployersByCompany(user, company).stream().map(employerMapper::mapToDto).toList(),
                new ArrayList<>()//licenseService.getLicensesByCompany(user, company).stream().map(licenseMapper::mapToDto).toList()
        ));
    }

    // TODO : setLogo

    @PostMapping(path = "/{comp_id}/employer/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Добавление работника со всеми полями и подписью")
    public ResponseEntity<Void> addEmployer(@PathVariable("comp_id") long id,
                                          @RequestPart("employerDto") @Valid EmployerDto dto,
                                          @RequestPart("signature") MultipartFile signature,
                                          Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        employerService.addEmployer(user, employerMapper.mapToEmployer(dto), companyService.get(id), signature);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{comp_id}/employer/{emp_id}/delete")
    @Operation(summary = "Удаление работника")
    public ResponseEntity<Void> deleteEmployer(@PathVariable("comp_id") long compId,
                                             @PathVariable("emp_id") long empId,
                                             Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        employerService.deleteEmployer(user, companyService.get(compId), empId);
        return ResponseEntity.ok().build();
    }

    // TODO: updateEmployer

    @PostMapping("/{comp_id}/license/add")
    @Operation(summary = "Добавление лицензии") // TODO : FIX!
    public ResponseEntity<Void> addLicense(@PathVariable("comp_id") long id,
                                           @RequestBody LicenseDto dto,
                                           Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        licenseService.addLicense(user, companyService.get(id), licenseMapper.mapToLicense(dto));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{comp_id}/license/{lic_id}/pic/add")
    public ResponseEntity<Void> addLicensePicture(@PathVariable("comp_id") long compId,
                                                  @PathVariable("lic_id") long licId,
                                                  MultipartFile scan,
                                                  Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        licenseService.addLicensePicture(user, companyService.get(compId), licId, scan);
        return ResponseEntity.ok().build();
    }

    // TODO : deleteLicense; updateLicense
    // TODO : Все, что связано с СРО
}
