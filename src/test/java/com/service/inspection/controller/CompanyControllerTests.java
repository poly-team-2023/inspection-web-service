package com.service.inspection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.inspection.InspectionApplication;
import com.service.inspection.configs.BucketName;
import com.service.inspection.dto.company.CompanyDto;
import com.service.inspection.dto.employer.EmployerDto;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.Employer;
import com.service.inspection.entities.Identifiable;
import com.service.inspection.entities.User;
import com.service.inspection.mapper.CommonMapper;
import com.service.inspection.mapper.CompanyMapper;
import com.service.inspection.mapper.EmployerMapper;
import com.service.inspection.mapper.LicenseMapper;
import com.service.inspection.repositories.EmployerRepository;
import com.service.inspection.repositories.UserRepository;
import com.service.inspection.service.AbstractTestContainerStartUp;
import com.service.inspection.service.AuthService;
import com.service.inspection.service.CompanyService;
import com.service.inspection.service.EmployerService;
import com.service.inspection.service.LicenseService;
import com.service.inspection.service.StorageService;
import com.service.inspection.utils.ControllerUtils;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthController authController;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private EmployerService employerService;

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private EmployerMapper employerMapper;

    @Autowired
    private LicenseMapper licenseMapper;

    @Autowired
    private ControllerUtils controllerUtils;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private StorageService storageService;

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users", "user_roles");
    }

    @Test
    void companyBasicActionsTest() {
        User user = new User();
        user.setFirstName("test");
        user.setSecondName("test");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEmail("test@example.com");
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
        User user = new User();
        user.setFirstName("test");
        user.setSecondName("test");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEmail("test@example.com");
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
        User user = new User();
        user.setFirstName("test");
        user.setSecondName("test");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEmail("test@example.com");
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
    @Transactional
    void employeeAddDeleteTest() {
        User user = new User();
        user.setFirstName("test");
        user.setSecondName("test");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEmail("test@example.com");
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
    @Transactional
    @Disabled
    void employeeUpdateTest() {
        User user = new User();
        user.setFirstName("test");
        user.setSecondName("test");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEmail("test@example.com");
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

        EmployerDto employerDto = new EmployerDto();
        employerDto.setName(name + 1);
        employerDto.setPositionName(pos + 1);

        employerService.updateEmployer(savedUser.getId(), companyWithId.getId(), employerWithId.getId(), employerDto);
        Optional<Employer> employerOptional = employerRepository.findById(employerWithId.getId());

        assert employerOptional.isPresent();
        Employer employer = employerOptional.get();

        assertEquals(employer.getName(), name + 1);
        assertEquals(employer.getPositionName(), pos + 1);
    }
}
