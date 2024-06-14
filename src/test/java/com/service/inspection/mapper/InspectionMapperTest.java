package com.service.inspection.mapper;
import com.service.inspection.dto.inspection.GetInspectionDto;
import com.service.inspection.dto.inspection.InspectionDto;
import com.service.inspection.dto.inspection.InspectionWithName;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.Employer;
import com.service.inspection.entities.Inspection;
import com.service.inspection.entities.enums.BuildingType;
import com.service.inspection.repositories.CategoryRepository;
import com.service.inspection.repositories.CompanyRepository;
import com.service.inspection.repositories.EmployerRepository;
import com.service.inspection.repositories.PlanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {InspectionMapperImpl.class, EntityFactory.class})
class InspectionMapperTest {

    @Autowired
    private InspectionMapper inspectionMapper;

    @MockBean
    private CompanyRepository companyRepository;
    @MockBean
    private EmployerRepository employerRepository;
    @MockBean
    private PlanRepository planRepository;
    @MockBean
    private CategoryRepository categoryRepository;

    @Test
    void whenMapToInspectionWithName_givenInspection_shouldReturnInspectionWithName() {
        // Arrange
        Inspection inspection = new Inspection();
        inspection.setId(1L);
        inspection.setName("TestInspection");

        // Act
        InspectionWithName result = inspectionMapper.mapToInspectionWithName(inspection);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("TestInspection");
    }

    @Test
    void whenMapToInspectionWithName_givenNullInspection_shouldReturnNull() {
        // Act
        InspectionWithName result = inspectionMapper.mapToInspectionWithName(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void whenMapToInspection_givenInspectionAndInspectionDto_shouldUpdateInspection() {
        // Arrange
        Inspection inspection = new Inspection();
        InspectionDto inspectionDto = new InspectionDto();
        inspectionDto.setCompanyId(1L);
        inspectionDto.setEmployerId(2L);
        inspectionDto.setIsCulture(true);
        inspectionDto.setName("UpdatedInspection");
        inspectionDto.setStartDate(LocalDate.now());
        inspectionDto.setEndDate(LocalDate.now().plusDays(1));
        inspectionDto.setAddress("UpdatedAddress");
        inspectionDto.setScript("UpdatedScript");

        inspectionMapper.mapToInspection(inspection, inspectionDto);

        assertThat(inspection.getName()).isEqualTo("UpdatedInspection");
        assertThat(inspection.getAddress()).isEqualTo("UpdatedAddress");
        assertThat(inspection.getScript()).isEqualTo("UpdatedScript");
    }

    @Test
    void whenMapToGetInspectionDto_givenInspection_shouldReturnGetInspectionDto() {

        Company company = new Company();
        company.setId(1L);
        company.setName("TestCompany");

        Employer employer = new Employer();
        employer.setId(2L);
        employer.setName("TestEmployer");

        // Arrange
        Inspection inspection = new Inspection();
        inspection.setId(1L);
        inspection.setName("TestInspection");
        inspection.setBuildingType(BuildingType.CULTURE);
        inspection.setAddress("TestAddress");
        inspection.setScript("TestScript");
        inspection.setStartDate(LocalDate.now());
        inspection.setEndDate(LocalDate.now().plusDays(1));
        inspection.setCompany(company);
        inspection.setEmployer(employer);

        // Act
        GetInspectionDto result = inspectionMapper.mapToGetInspectionDto(inspection);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getIsCulture()).isEqualTo(inspectionMapper.toIsCultureBuildingType(BuildingType.CULTURE)); // Assume toIsCultureBuildingType method exists and works correctly
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("TestInspection");
        assertThat(result.getAddress()).isEqualTo("TestAddress");
        assertThat(result.getScript()).isEqualTo("TestScript");
        assertThat(result.getStartDate()).isEqualTo(LocalDate.now());
        assertThat(result.getEndDate()).isEqualTo(LocalDate.now().plusDays(1));
        assertThat(result.getCompany()).extracting("id", "name").containsExactly(1L, "TestCompany");
        assertThat(result.getEmployer()).extracting("id", "name").containsExactly(2L, "TestEmployer");
    }

    @Test
    void whenMapToGetInspectionDto_givenNullInspection_shouldReturnNull() {
        // Act
        GetInspectionDto result = inspectionMapper.mapToGetInspectionDto(null);

        // Assert
        assertThat(result).isNull();
    }
}
