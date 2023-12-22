package com.service.inspection.controller;

import com.service.inspection.InspectionApplication;
import com.service.inspection.configs.BucketName;
import com.service.inspection.dto.company.CompanyDto;
import com.service.inspection.dto.employer.EmployerDto;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.Employer;
import com.service.inspection.entities.Identifiable;
import com.service.inspection.entities.License;
import com.service.inspection.entities.User;
import com.service.inspection.mapper.EmployerMapper;
import com.service.inspection.repositories.EmployerRepository;
import com.service.inspection.repositories.LicenseRepository;
import com.service.inspection.repositories.UserRepository;
import com.service.inspection.service.AbstractTestContainerStartUp;
import com.service.inspection.service.CompanyService;
import com.service.inspection.service.EmployerService;
import com.service.inspection.service.LicenseService;
import com.service.inspection.service.StorageService;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = InspectionApplication.class)
@AutoConfigureMockMvc
public class CompanyControllerTests extends AbstractTestContainerStartUp {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private EmployerService employerService;

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private EmployerMapper employerMapper;

    @Autowired
    private StorageService storageService;

    @Autowired
    private LicenseRepository licenseRepository;

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "users", "user_roles", "company", "employer", "license");
    }

    @Test
    void companyBasicActionsTest() {
        User user = userRepo.save(getUser(1));

        companyService.createCompany(user);

        Company company = getSingleCompany(user.getId());
        assertEquals(company.getUser(), user);

        companyService.deleteCompany(user.getId(), company.getId());

        List<Company> companies = companyService.getCompanies(user.getId());
        assertEquals(companies.size(), 0);
    }

    private Company getSingleCompany(long id) {
        List<Company> companies = companyService.getCompanies(id);
        assertEquals(companies.size(), 1);

        return companies.get(0);
    }

    @Test
    void createLogo_checkContent() {
        User user = userRepo.save(getUser(1));

        Identifiable companyWithId = companyService.createCompany(user);

        String fileContent = "This is the content of the file";
        MultipartFile logo = getFile(fileContent);
        companyService.addLogo(user.getId(), companyWithId.getId(), logo);

        Company savedCompany = getSingleCompany(user.getId());
        StorageService.BytesWithContentType logoBytes =
                storageService.getFile(BucketName.COMPANY_LOGO, savedCompany.getLogoUuid().toString());
        String savedFileContent = new String(logoBytes.getBytes());

        assertEquals(fileContent, savedFileContent);
    }

    @Test
    void updateCompany() {
        User user = userRepo.save(getUser(1));

        Identifiable companyWithId = companyService.createCompany(user);

        Company savedCompany = getSingleCompany(user.getId());
        assertNull(savedCompany.getName(), "");
        assertNull(savedCompany.getCity(), "");
        assertNull(savedCompany.getLegalAddress(), "");

        CompanyDto companyDto = new CompanyDto();
        companyDto.setName("name");
        companyDto.setCity("city");
        companyDto.setLegalAddress("address");

        companyService.updateCompany(user.getId(), companyWithId.getId(), companyDto);
        savedCompany = getSingleCompany(user.getId());
        assertEquals(savedCompany.getName(), "name");
        assertEquals(savedCompany.getCity(), "city");
        assertEquals(savedCompany.getLegalAddress(), "address");
    }

    @Test
    void employeeAddDeleteTest() {
        User user = userRepo.save(getUser(1));

        Identifiable companyWithId = companyService.createCompany(user);

        List<Employer> employers = new ArrayList<>();
        int employersCount = 4;
        for (int i = 0; i < employersCount; i++) {
            Employer employer = new Employer();
            employer.setPositionName("pos_" + i);
            employer.setName("name_" + i);
            employers.add(employer);
        }

        MultipartFile sign = getFile("sign");
        for (Employer employer : employers) {
            Identifiable employerWithId = employerService.addEmployer(
                    user.getId(),
                    employerMapper.mapToEmployer(employer.getName(), employer.getPositionName()),
                    companyWithId.getId(),
                    sign
            );
            employer.setId(employerWithId.getId());
        }

        for (int i = 0; i < employersCount; i++) {
            long employeeId = employers.get(i).getId();
            employerService.deleteEmployer(user.getId(), companyWithId.getId(), employeeId);
            assertTrue(employerRepository.findById(employeeId).isEmpty());

            int existedEmployersCount = employerRepository.findAllByCompanyId(companyWithId.getId()).size();
            int expectedEmployersCount = employersCount - i - 1;
            assertEquals(expectedEmployersCount, existedEmployersCount);
        }
    }

    @Test
    void employeeUpdateTest() {
        User user = userRepo.save(getUser(1));

        Identifiable companyWithId = companyService.createCompany(user);

        String name = "name";
        String pos = "pos";
        MultipartFile sign = getFile("sign");
        Identifiable employerWithId = employerService.addEmployer(
                user.getId(),
                employerMapper.mapToEmployer(name, pos),
                companyWithId.getId(),
                sign
        );

        // Проверяем, что сотрудник единственный.
        int employersCount = employerRepository.findAllByCompanyId(companyWithId.getId()).size();
        assertEquals(employersCount, 1);

        EmployerDto employerDto = new EmployerDto();
        employerDto.setName(name + 1);
        employerDto.setPositionName(pos + 1);

        employerService.updateEmployer(
                user.getId(),
                companyWithId.getId(),
                employerWithId.getId(),
                employerDto,
                getFile("file")
        );

        Optional<Employer> employerOptional = employerRepository.findById(employerWithId.getId());
        assert employerOptional.isPresent();
        Employer employer = employerOptional.get();

        // Проверяем, что сотрудник имеет актуальные данные.
        assertEquals(employer.getName(), name + 1);
        assertEquals(employer.getPositionName(), pos + 1);

        // Проверяем, что сотрудник единственный.
        employersCount = employerRepository.findAllByCompanyId(companyWithId.getId()).size();
        assertEquals(employersCount, 1);
    }

    @Test
    @Transactional
    void licenceTest() {
        User user = userRepo.save(getUser(1));

        Identifiable companyWithId = companyService.createCompany(user);

        License license = new License();
        license.setName("licence_name");

        Identifiable licenceWithId = licenseService.addLicense(user.getId(), companyWithId.getId(), license);
        licenseService.addLicense(user.getId(), companyWithId.getId(), license); // it's ok.

        List<License> licences = licenseRepository.findAll();
        assertEquals(licences.size(), 1);
        assertEquals(licences.get(0).getName(), "licence_name");

        licenseService.deleteLicense(user.getId(), companyWithId.getId(), licenceWithId.getId());

        assertTrue(licenseRepository.findAll().isEmpty());
    }

    @Test
    @Transactional
    void sroBaseTest() {
        User user = userRepo.save(getUser(1));

        Identifiable companyWithId = companyService.createCompany(user);

        License license = new License();
        license.setName("licence_name");

        String sroContent = "sro";
        Identifiable sroIdentifiable =
                companyService.addSro(user.getId(), companyWithId.getId(), 1, getFile(sroContent));
        StorageService.BytesWithContentType sroBytes = companyService.getSroScan(
                companyWithId.getId(),
                user.getId(),
                sroIdentifiable.getId()
        );

        assertEquals(new String(sroBytes.getBytes()), sroContent);

        companyService.deleteSro(user.getId(), companyWithId.getId(), sroIdentifiable.getId());

        Assertions.assertThatThrownBy(
                () -> companyService.getSroScan(
                        companyWithId.getId(),
                        user.getId(),
                        sroIdentifiable.getId())
        ).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @Transactional
    void sroReplacementTest() {
        User savedUser = userRepo.save(getUser(1));

        Identifiable companyWithId = companyService.createCompany(savedUser);

        License license = new License();
        license.setName("licence_name");

        companyService.addSro(savedUser.getId(), companyWithId.getId(), 1, getFile("sro"));

        String sroNewContent = "sro_new";
        Identifiable sroIdentifiable =
                companyService.addSro(savedUser.getId(), companyWithId.getId(), 1, getFile(sroNewContent));
        StorageService.BytesWithContentType sroBytes =
                companyService.getSroScan(companyWithId.getId(), savedUser.getId(), sroIdentifiable.getId());

        assertEquals(new String(sroBytes.getBytes()), sroNewContent);
    }

    private User getUser(int prefix) {
        User user = new User();

        user.setFirstName("test" + prefix);
        user.setSecondName("test" + prefix);
        user.setEmail("test" + prefix + "@example.com");
        user.setPassword(passwordEncoder.encode("password"));

        return user;
    }

    private MultipartFile getFile(String content) {
        String name = "file";
        String originalFileName = "sample.txt";
        String contentType = "text/plain";

        return new MockMultipartFile(
                name,
                originalFileName,
                contentType,
                content.getBytes()
        );
    }
}
