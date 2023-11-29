package com.service.inspection.controller;

import com.service.inspection.dto.IdentifiableDto;
import com.service.inspection.dto.company.CompanyDto;
import com.service.inspection.dto.company.GetCompanyDto;
import com.service.inspection.dto.employer.EmployerDto;
import com.service.inspection.dto.license.LicenseDto;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.Identifiable;
import com.service.inspection.entities.User;
import com.service.inspection.mapper.CommonMapper;
import com.service.inspection.mapper.CompanyMapper;
import com.service.inspection.mapper.EmployerMapper;
import com.service.inspection.mapper.LicenseMapper;
import com.service.inspection.service.CompanyService;
import com.service.inspection.service.EmployerService;
import com.service.inspection.service.LicenseService;
import com.service.inspection.service.security.UserDetailsImpl;
import com.service.inspection.utils.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/company")
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final EmployerService employerService;
    private final LicenseService licenseService;
    private final CompanyMapper companyMapper;
    private final EmployerMapper employerMapper;
    private final LicenseMapper licenseMapper;
    private final ControllerUtils controllerUtils;
    private final CommonMapper commonMapper;

    @PostMapping
    @Operation(summary = "Создать компанию")
    public ResponseEntity<IdentifiableDto> createCompany(Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        return ResponseEntity.ok(commonMapper.mapToIdentifiableDto(companyService.createCompany(user)));
    }

    @GetMapping
    @Operation(summary = "Получить компании")
    public ResponseEntity<List<GetCompanyDto>> getCompanies(Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        return ResponseEntity.ok(companyService.getCompanies(user.getId())
                .stream().map(companyMapper::mapToDto).toList());
    }

    @PutMapping("/{comp_id}")
    @Operation(summary = "Обновление текстовых полей")
    public ResponseEntity<Void> updateCompany(@PathVariable("comp_id") long id,
                                              @RequestBody @Valid CompanyDto dto,
                                              Authentication authentication) {
        companyService.updateCompany(controllerUtils.getUserId(authentication), id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{comp_id}")
    @Operation(summary = "Удаление компании")
    public ResponseEntity<Void> deleteCompany(@PathVariable("comp_id") long id,
                                              Authentication authentication) {
        companyService.deleteCompany(controllerUtils.getUserId(authentication), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{comp_id}")
    @Operation(summary = "Получить информацию о компании")
    public ResponseEntity<GetCompanyDto> getCompany(@PathVariable("comp_id") long id) {
        Company company = companyService.get(id);
        return ResponseEntity.ok(companyMapper.mapToDto(company));
    }

    @PostMapping(path = "/{comp_id}/logo")
    @Operation(summary = "Добавить лого компапнии")
    public ResponseEntity<Void> setLogo(@PathVariable("comp_id") long id,
                                        @RequestParam("file") MultipartFile picture,
                                        Authentication authentication) {
        companyService.addLogo(controllerUtils.getUserId(authentication), id, picture);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/{comp_id}/employer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Добавление работника со всеми полями и подписью")
    public ResponseEntity<IdentifiableDto> addEmployer(@PathVariable("comp_id") long id,
                                                       @RequestParam("name") @NotBlank String name,
                                                       @RequestParam("position") @NotBlank String position,
                                                       MultipartFile signature,
                                                       Authentication authentication) {
        Identifiable employer = employerService.addEmployer(
                controllerUtils.getUserId(authentication),
                employerMapper.mapToEmployer(name, position),
                id,
                signature
        );
        return ResponseEntity.ok(commonMapper.mapToIdentifiableDto(employer));
    }

    @DeleteMapping("/{comp_id}/employer/{emp_id}")
    @Operation(summary = "Удаление работника")
    public ResponseEntity<Void> deleteEmployer(@PathVariable("comp_id") long compId,
                                               @PathVariable("emp_id") long empId,
                                               Authentication authentication) {
        employerService.deleteEmployer(controllerUtils.getUserId(authentication), compId, empId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{comp_id}/employer/{emp_id}")
    @Operation(summary = "Обновление работника")
    public ResponseEntity<Void> updateEmployer(@PathVariable("comp_id") long compId,
                                               @PathVariable("emp_id") long empId,
                                               @RequestBody @Valid EmployerDto dto,
                                               Authentication authentication) {
        employerService.updateEmployer(controllerUtils.getUserId(authentication), compId, empId, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{comp_id}/license")
    @Operation(summary = "Добавление лицензии")
    public ResponseEntity<IdentifiableDto> addLicense(@PathVariable("comp_id") long id,
                                                      @RequestBody LicenseDto dto,
                                                      Authentication authentication) {
        return ResponseEntity.ok(commonMapper.mapToIdentifiableDto(licenseService.addLicense(
                controllerUtils.getUserId(authentication), id, licenseMapper.mapToLicense(dto))));
    }

    @PostMapping("/{comp_id}/license/{lic_id}/pic")
    @Operation(summary = "Добавление картинки к лицензии")
    public ResponseEntity<Void> addLicensePicture(@PathVariable("comp_id") long compId,
                                                  @PathVariable("lic_id") long licId,
                                                  MultipartFile scan,
                                                  Authentication authentication) {
        licenseService.addLicensePicture(controllerUtils.getUserId(authentication), compId, licId, scan);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{comp_id}/license/{lic_id}")
    @Operation(summary = "Обновить лицензию")
    public ResponseEntity<Void> updateLicense(@PathVariable("comp_id") long compId,
                                              @PathVariable("lic_id") long licId,
                                              @RequestBody LicenseDto dto,
                                              Authentication authentication) {
        licenseService.updateLicense(controllerUtils.getUserId(authentication), compId, licId, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{comp_id}/license/{lic_id}")
    @Operation(summary = "Удалить лицензии")
    public ResponseEntity<Void> deleteLicense(@PathVariable("comp_id") long compId,
                                              @PathVariable("lic_id") long licId,
                                              Authentication authentication) {
        licenseService.deleteLicense(controllerUtils.getUserId(authentication), compId, licId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{comp_id}/sro")
    @Operation(summary = "Добавить сро")
    public ResponseEntity<Void> addSro(@PathVariable("comp_id") long compId,
                                       MultipartFile picture,
                                       Authentication authentication) {
        companyService.addSro(controllerUtils.getUserId(authentication), compId, picture);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{comp_id}/sro")
    @Operation(summary = "Удалить сро")
    public ResponseEntity<Void> deleteSro(@PathVariable("comp_id") long compId,
                                          Authentication authentication) {
        companyService.deleteSro(controllerUtils.getUserId(authentication), compId);
        return ResponseEntity.ok().build();
    }
}
