package com.service.inspection.controller;

import com.service.inspection.configs.security.jwt.AuthTokenFilter;
import com.service.inspection.dto.IdentifiableDto;
import com.service.inspection.dto.company.CompanyDto;
import com.service.inspection.dto.company.GetCompanyDto;
import com.service.inspection.dto.employer.EmployerDto;
import com.service.inspection.dto.license.LicenseDto;
import com.service.inspection.entities.*;
import com.service.inspection.mapper.CommonMapper;
import com.service.inspection.mapper.CompanyMapper;
import com.service.inspection.mapper.EmployerMapper;
import com.service.inspection.mapper.LicenseMapper;
import com.service.inspection.service.CompanyService;
import com.service.inspection.service.EmployerService;
import com.service.inspection.service.LicenseService;
import com.service.inspection.service.StorageService;
import com.service.inspection.utils.ControllerUtils;
import com.service.inspection.utils.ServiceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.service.inspection.controller.InspectionControllerTest.asJsonString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CompanyController.class,
        excludeFilters = @ComponentScan.Filter(classes = AuthTokenFilter.class, type = FilterType.ASSIGNABLE_TYPE))
@WithMockUser
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyService companyService;
    @MockBean
    private EmployerService employerService;
    @MockBean
    private LicenseService licenseService;
    @MockBean
    private ServiceUtils serviceUtils;
    @MockBean
    private CompanyMapper companyMapper;
    @MockBean
    private EmployerMapper employerMapper;
    @MockBean
    private LicenseMapper licenseMapper;
    @MockBean
    private ControllerUtils controllerUtils;
    @MockBean
    private CommonMapper commonMapper;

    @BeforeEach
    public void setUp() {
        when(controllerUtils.getUserId(any())).thenReturn(1L);

        User user = new User();
        user.setId(1L);
        when(controllerUtils.getUser(any())).thenReturn(user);
    }

    @Test
    void testCreateCompany() throws Exception {
        IdentifiableDto identifiableDto = new IdentifiableDto(1L);

        when(companyService.createCompany(any(User.class))).thenReturn(new Company());
        when(commonMapper.mapToIdentifiableDto(any(Company.class))).thenReturn(identifiableDto);

        mockMvc.perform(post("/api/v1/company")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(identifiableDto.getId()));
    }

    @Test
    void testGetCompanies() throws Exception {

        GetCompanyDto companyDtoList = new GetCompanyDto();
        companyDtoList.setId(1L);

        when(companyService.getCompanies(anyLong())).thenReturn(List.of(new Company()));
        when(companyMapper.mapToDto(any())).thenReturn(companyDtoList);

        mockMvc.perform(get("/api/v1/company"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void testUpdateCompany() throws Exception {
        long id = 1L;
        CompanyDto companyDto = new CompanyDto();
        companyDto.setName("Updated Company");
        companyDto.setLegalAddress("Legal address");


        doNothing().when(companyService).updateCompany(anyLong(), eq(id), any(CompanyDto.class));

        mockMvc.perform(put("/api/v1/company/{comp_id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(companyDto)).with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteCompany() throws Exception {
        long id = 1L;

        doNothing().when(companyService).deleteCompany(anyLong(), eq(id));

        mockMvc.perform(delete("/api/v1/company/{comp_id}", id).with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCompany() throws Exception {
        long id = 1L;
        GetCompanyDto getCompanyDto = new GetCompanyDto();
        getCompanyDto.setId(id);
        getCompanyDto.setName("Test Company");

        when(serviceUtils.getCompanyIfExistForUser(anyLong(), eq(id))).thenReturn(new Company());
        when(companyMapper.mapToDto(any(Company.class))).thenReturn(getCompanyDto);

        mockMvc.perform(get("/api/v1/company/{comp_id}", id).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getCompanyDto.getId()))
                .andExpect(jsonPath("$.name").value(getCompanyDto.getName()));
    }

    @Test
    void testSetLogo() throws Exception {
        long id = 1L;
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file", "logo.png", MediaType.IMAGE_PNG_VALUE, "logo content".getBytes());

        doNothing().when(companyService).addLogo(anyLong(), eq(id), any(MultipartFile.class));

        mockMvc.perform(multipart("/api/v1/company/{comp_id}/logo", id)
                        .file(mockMultipartFile).with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetLogo() throws Exception {
        long id = 1L;
        StorageService.BytesWithContentType file = new StorageService.BytesWithContentType(
                "logo content".getBytes(), MediaType.IMAGE_PNG_VALUE);

        when(companyService.getLogo(eq(id), anyLong())).thenReturn(file);
        when(controllerUtils.getResponseEntityFromFile(anyString(), any(StorageService.BytesWithContentType.class)))
                .thenCallRealMethod();

        mockMvc.perform(get("/api/v1/company/{comp_id}/logo", id).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE));
    }

    @Test
    void testDeleteLogo() throws Exception {
        long id = 1L;

        doNothing().when(companyService).deleteLogo(anyLong(), eq(id));

        mockMvc.perform(delete("/api/v1/company/{comp_id}/logo", id)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testAddEmployer() throws Exception {
        long id = 1L;
        String name = "John Doe";
        String position = "Developer";
        MockMultipartFile signature = new MockMultipartFile(
                "signature", "signature.png", MediaType.IMAGE_PNG_VALUE, "signature content".getBytes());
        IdentifiableDto identifiableDto = new IdentifiableDto(1L);


        when(employerMapper.mapToEmployer(name, position)).thenReturn(new Employer());
        when(employerService.addEmployer(anyLong(), any(Employer.class), eq(id), any(MultipartFile.class)))
                .thenReturn(new Identifiable());
        when(commonMapper.mapToIdentifiableDto(any(Identifiable.class))).thenReturn(identifiableDto);

        mockMvc.perform(multipart("/api/v1/company/{comp_id}/employer", id)
                        .file(signature)
                        .param("name", name)
                        .param("position", position)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(identifiableDto.getId()));
    }

    @Test
    void testDeleteEmployer() throws Exception {
        long compId = 1L;
        long empId = 2L;

        doNothing().when(employerService).deleteEmployer(anyLong(), eq(compId), eq(empId));

        mockMvc.perform(delete("/api/v1/company/{comp_id}/employer/{emp_id}", compId, empId)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateEmployer() throws Exception {
        long compId = 1L;
        long empId = 2L;
        String name = "Jane Doe";
        String position = "Manager";
        MockMultipartFile signature = new MockMultipartFile(
                "signature", "signature.png", MediaType.IMAGE_PNG_VALUE, "updated signature content".getBytes());

        doNothing().when(employerService).updateEmployer(anyLong(), eq(compId), eq(empId), any(EmployerDto.class), any(MultipartFile.class));

        mockMvc.perform(multipart("/api/v1/company/{comp_id}/employer/{emp_id}", compId, empId)
                .file(signature)
                .param("name", name)
                .param("position", position)
                .with(csrf())
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                })).andExpect(status().isOk());
    }

    @Test
    void testGetSignature() throws Exception {
        long compId = 1L;
        long empId = 2L;
        StorageService.BytesWithContentType file = new StorageService.BytesWithContentType(
                "signature content".getBytes(), MediaType.IMAGE_PNG_VALUE);

        when(employerService.getSignature(anyLong(), eq(compId), eq(empId))).thenReturn(file);
        when(controllerUtils.getResponseEntityFromFile(anyString(), any(StorageService.BytesWithContentType.class)))
                .thenCallRealMethod();

        mockMvc.perform(get("/api/v1/company/{comp_id}/employer/{emp_id}/signature", compId, empId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE));
    }

    @Test
    void testAddLicense() throws Exception {
        long id = 1L;
        LicenseDto licenseDto = new LicenseDto();
        licenseDto.setName("123-456");
        IdentifiableDto identifiableDto = new IdentifiableDto(1L);

        when(licenseService.addLicense(anyLong(), eq(id), any(License.class)))
                .thenReturn(new License());
        when(licenseMapper.mapToLicense(licenseDto)).thenReturn(new License());
        when(commonMapper.mapToIdentifiableDto(any(License.class))).thenReturn(identifiableDto);

        mockMvc.perform(post("/api/v1/company/{comp_id}/license", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(licenseDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(identifiableDto.getId()));
    }

    @Test
    void testAddLicenseScan() throws Exception {
        long compId = 1L;
        long licId = 2L;
        int scanNumber = 1;
        MockMultipartFile scan = new MockMultipartFile(
                "scan", "license_scan.pdf", MediaType.APPLICATION_PDF_VALUE, "scan content".getBytes());
        IdentifiableDto identifiableDto = new IdentifiableDto(1L);

        when(licenseService.addLicenseScan(anyLong(), eq(compId), eq(licId), eq(scanNumber), any(MultipartFile.class)))
                .thenReturn(new Identifiable());
        when(commonMapper.mapToIdentifiableDto(any(Identifiable.class))).thenReturn(identifiableDto);

        mockMvc.perform(multipart("/api/v1/company/{comp_id}/license/{lic_id}/scan", compId, licId)
                        .file(scan)
                        .param("scanNumber", String.valueOf(scanNumber))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(identifiableDto.getId()));
    }

    @Test
    void testUpdateLicenseScan() throws Exception {
        long compId = 1L;
        long licId = 2L;
        long scanId = 3L;
        int scanNumber = 2;

        doNothing().when(licenseService).updateLicenseScan(anyLong(), eq(compId), eq(licId), eq(scanId), eq(scanNumber));

        mockMvc.perform(put("/api/v1/company/{comp_id}/license/{lic_id}/scan/{scan_id}", compId, licId, scanId)
                        .param("scanNumber", String.valueOf(scanNumber))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteLicenseScan() throws Exception {
        long compId = 1L;
        long licId = 2L;
        long scanId = 3L;

        doNothing().when(licenseService).deleteLicenseScan(anyLong(), eq(compId), eq(licId), eq(scanId));

        mockMvc.perform(delete("/api/v1/company/{comp_id}/license/{lic_id}/scan/{scan_id}", compId, licId, scanId)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteAllLicenseScan() throws Exception {
        long compId = 1L;
        long licId = 2L;

        doNothing().when(licenseService).deleteAllLicenseScan(anyLong(), eq(compId), eq(licId));

        mockMvc.perform(delete("/api/v1/company/{comp_id}/license/{lic_id}/scan", compId, licId)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetLicenseScan() throws Exception {
        long compId = 1L;
        long licId = 2L;
        long scanId = 3L;
        StorageService.BytesWithContentType file = new StorageService.BytesWithContentType(
                "scan content".getBytes(), MediaType.APPLICATION_PDF_VALUE);

        when(licenseService.getLicenseScan(eq(compId), anyLong(), eq(licId), eq(scanId))).thenReturn(file);
        when(controllerUtils.getResponseEntityFromFile(anyString(), any(StorageService.BytesWithContentType.class)))
                .thenCallRealMethod();

        mockMvc.perform(get("/api/v1/company/{comp_id}/license/{lic_id}/scan/{scan_id}", compId, licId, scanId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF_VALUE));
    }

    @Test
    void testUpdateLicense() throws Exception {
        long compId = 1L;
        long licId = 2L;
        LicenseDto licenseDto = new LicenseDto();

        doNothing().when(licenseService).updateLicense(anyLong(), eq(compId), eq(licId), any(LicenseDto.class));

        mockMvc.perform(put("/api/v1/company/{comp_id}/license/{lic_id}", compId, licId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(licenseDto))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteLicense() throws Exception {
        long compId = 1L;
        long licId = 2L;

        doNothing().when(licenseService).deleteLicense(anyLong(), eq(compId), eq(licId));

        mockMvc.perform(delete("/api/v1/company/{comp_id}/license/{lic_id}", compId, licId)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testAddSro() throws Exception {
        long compId = 1L;
        int scanNumber = 1;
        MockMultipartFile picture = new MockMultipartFile(
                "picture", "sro.png", MediaType.IMAGE_PNG_VALUE, "sro content".getBytes());
        IdentifiableDto identifiableDto = new IdentifiableDto(1L);

        when(companyService.addSro(anyLong(), eq(compId), eq(scanNumber), any(MultipartFile.class)))
                .thenReturn(new Identifiable());
        when(commonMapper.mapToIdentifiableDto(any(Identifiable.class))).thenReturn(identifiableDto);

        mockMvc.perform(multipart("/api/v1/company/{comp_id}/sro", compId)
                        .file(picture)
                        .param("scanNumber", String.valueOf(scanNumber))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(identifiableDto.getId()));
    }

    @Test
    void testDeleteSro() throws Exception {
        long compId = 1L;
        long sroId = 2L;

        doNothing().when(companyService).deleteSro(anyLong(), eq(compId), eq(sroId));

        mockMvc.perform(delete("/api/v1/company/{comp_id}/sro/{sro_id}", compId, sroId)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteAllSro() throws Exception {
        long compId = 1L;

        doNothing().when(companyService).deleteAllSro(anyLong(), eq(compId));

        mockMvc.perform(delete("/api/v1/company/{comp_id}/sro", compId)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateSro() throws Exception {
        long compId = 1L;
        long sroId = 2L;
        int scanNumber = 3;

        doNothing().when(companyService).updateSro(anyLong(), eq(compId), eq(sroId), eq(scanNumber));

        mockMvc.perform(put("/api/v1/company/{comp_id}/sro/{sro_id}", compId, sroId)
                        .param("scanNumber", String.valueOf(scanNumber))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetSro() throws Exception {
        long compId = 1L;
        long sroId = 2L;
        StorageService.BytesWithContentType file = new StorageService.BytesWithContentType(
                "sro content".getBytes(), MediaType.IMAGE_PNG_VALUE);

        when(companyService.getSroScan(eq(compId), anyLong(), eq(sroId))).thenReturn(file);
        when(controllerUtils.getResponseEntityFromFile(anyString(), any(StorageService.BytesWithContentType.class)))
                .thenCallRealMethod();

        mockMvc.perform(get("/api/v1/company/{comp_id}/sro/{sro_id}", compId, sroId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE));
    }
}
