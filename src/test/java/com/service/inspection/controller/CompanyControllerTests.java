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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.jdbc.JdbcTestUtils;
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

    private static final User user = new User();

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users", "user_roles");
    }

    @BeforeAll
    static void initUser() {
        user.setFirstName("test");
        user.setSecondName("test");
        user.setEmail("test@example.com");
    }

    @Test
    void companyBasicActionsTest() {
        user.setPassword(passwordEncoder.encode("password"));
        User savedUser = userRepo.save(user);

        companyService.createCompany(savedUser);

        Company company = getSingleCompany(savedUser.getId());
        assertEquals(company.getUser(), user);

        companyService.deleteCompany(user.getId(), company.getId());

        List<Company> companies = companyService.getCompanies(savedUser.getId());
        assertEquals(companies.size(), 0);
    }

    private Company getSingleCompany(long id) {
        List<Company> companies = companyService.getCompanies(id);
        assertEquals(companies.size(), 1);

        return companies.get(0);
    }

    @Test
    void createLogo_checkContent() {
        user.setPassword(passwordEncoder.encode("password"));
        User savedUser = userRepo.save(user);

        Identifiable companyWithId = companyService.createCompany(savedUser);

        String fileContent = "This is the content of the file";
        MultipartFile logo = getFile(fileContent);
        companyService.addLogo(savedUser.getId(), companyWithId.getId(), logo);

        Company savedCompany = getSingleCompany(savedUser.getId());
        StorageService.BytesWithContentType logoBytes =
                storageService.getFile(BucketName.COMPANY_LOGO, savedCompany.getLogoUuid().toString());
        String savedFileContent = new String(logoBytes.getBytes());

        assertEquals(fileContent, savedFileContent);
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

    @Test
    void updateCompany() {
        user.setPassword(passwordEncoder.encode("password"));
        User savedUser = userRepo.save(user);

        Identifiable companyWithId = companyService.createCompany(savedUser);

        Company savedCompany = getSingleCompany(savedUser.getId());
        assertNull(savedCompany.getName(), "");
        assertNull(savedCompany.getCity(), "");
        assertNull(savedCompany.getLegalAddress(), "");

        CompanyDto companyDto = new CompanyDto();
        companyDto.setName("name");
        companyDto.setCity("city");
        companyDto.setLegalAddress("address");

        companyService.updateCompany(savedUser.getId(), companyWithId.getId(), companyDto);
        savedCompany = getSingleCompany(savedUser.getId());
        assertEquals(savedCompany.getName(), "name");
        assertEquals(savedCompany.getCity(), "city");
        assertEquals(savedCompany.getLegalAddress(), "address");
    }

    @Test
    void employeeAddDeleteTest() {
        user.setPassword(passwordEncoder.encode("password"));
        User savedUser = userRepo.save(user);

        Identifiable companyWithId = companyService.createCompany(savedUser);

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
                    savedUser.getId(),
                    employerMapper.mapToEmployer(employer.getName(), employer.getPositionName()),
                    companyWithId.getId(),
                    sign
            );
            employer.setId(employerWithId.getId());
        }

        for (int i = 0; i < employersCount; i++) {
            long employeeId = employers.get(i).getId();
            employerService.deleteEmployer(savedUser.getId(), companyWithId.getId(), employeeId);
            assertTrue(employerRepository.findById(employeeId).isEmpty());

            int existedEmployersCount = employerRepository.findAllByCompanyId(companyWithId.getId()).size();
            int expectedEmployersCount = employersCount - i - 1;
            assertEquals(expectedEmployersCount, existedEmployersCount);
        }
    }

    @Test
    void employeeUpdateTest() {
        user.setPassword(passwordEncoder.encode("password"));
        User savedUser = userRepo.save(user);

        Identifiable companyWithId = companyService.createCompany(savedUser);

        String name = "name";
        String pos = "pos";
        MultipartFile sign = getFile("sign");
        Identifiable employerWithId = employerService.addEmployer(
                savedUser.getId(),
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

        employerService.updateEmployer(savedUser.getId(), companyWithId.getId(), employerWithId.getId(), employerDto);

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
    @Disabled
    void licenceTest() throws InterruptedException {
        user.setPassword(passwordEncoder.encode("password"));
        User savedUser = userRepo.save(user);

        Identifiable companyWithId = companyService.createCompany(savedUser);

        License license = new License();
        license.setName("licence_name");
        license.setNumber(123);

        Identifiable licenceWithId = licenseService.addLicense(savedUser.getId(), companyWithId.getId(), license);
        licenseService.addLicense(savedUser.getId(), companyWithId.getId(), license); // it's ok.

        List<License> licences = licenseRepository.findAll();
        assertEquals(licences.size(), 1);
        assertEquals(licences.get(0).getName(), "licence_name");
        assertEquals(licences.get(0).getNumber(), 123);

        licenseService.deleteLicense(savedUser.getId(), companyWithId.getId(), licenceWithId.getId());

        assertTrue(licenseRepository.findAll().isEmpty());
    }

    @Test
    void sroBaseTest() {
        user.setPassword(passwordEncoder.encode("password"));
        User savedUser = userRepo.save(user);

        Identifiable companyWithId = companyService.createCompany(savedUser);

        License license = new License();
        license.setName("licence_name");
        license.setNumber(123);

        String sroContent = "sro";
        MultipartFile sro = getFile(sroContent);
        companyService.addSro(savedUser.getId(), companyWithId.getId(), sro);
        StorageService.BytesWithContentType sroBytes = companyService.getSroScan(companyWithId.getId(), savedUser.getId());

        assertEquals(new String(sroBytes.getBytes()), sroContent);

        companyService.deleteSro(savedUser.getId(), companyWithId.getId());

        StorageService.BytesWithContentType sroBytesAfterDelete = companyService.getSroScan(companyWithId.getId(), savedUser.getId());
        assertNull(sroBytesAfterDelete);
    }

    @Test
    void sroReplacementTest() {
        user.setPassword(passwordEncoder.encode("password"));
        User savedUser = userRepo.save(user);

        Identifiable companyWithId = companyService.createCompany(savedUser);

        License license = new License();
        license.setName("licence_name");
        license.setNumber(123);

        companyService.addSro(savedUser.getId(), companyWithId.getId(), getFile("sro"));

        // Заменяем другим.
        String sroNewContent = "sro_new";
        companyService.addSro(savedUser.getId(), companyWithId.getId(), getFile(sroNewContent));
        StorageService.BytesWithContentType sroBytes = companyService.getSroScan(companyWithId.getId(), savedUser.getId());

        assertEquals(new String(sroBytes.getBytes()), sroNewContent);
    }
}
