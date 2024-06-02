package com.service.inspection.mapper;

import com.service.inspection.dto.company.CompanyDto;
import com.service.inspection.dto.company.GetCompanyDto;
import com.service.inspection.entities.Company;
import org.springframework.beans.factory.annotation.Autowired;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CompanyMapperImpl.class})
class CompanyMapperTest {

    @Autowired
    private CompanyMapper companyMapper;

    @Test
    void whenMapToUpdateCompany_givenSource_shouldUpdateCompany() {
        Company toUpdate = new Company();
        CompanyDto source = new CompanyDto();
        source.setName("Updated Company");
        source.setLegalAddress("Updated Address");
        source.setCity("Updated City");

        companyMapper.mapToUpdateCompany(toUpdate, source);

        assertThat(toUpdate.getName()).isEqualTo("Updated Company");
        assertThat(toUpdate.getLegalAddress()).isEqualTo("Updated Address");
        assertThat(toUpdate.getCity()).isEqualTo("Updated City");
    }

    @Test
    void whenMapToDto_givenCompany_shouldReturnGetCompanyDto() {
        // Arrange
        Company company = new Company();
        company.setId(1L);
        company.setName("Company");
        company.setLegalAddress("Legal Address");
        company.setCity("City");
        company.setFilesSro(new HashSet<>());
        company.setEmployers(new HashSet<>());
        company.setLicenses(new HashSet<>());

        // Act
        GetCompanyDto result = companyMapper.mapToDto(company);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Company");
        assertThat(result.getLegalAddress()).isEqualTo("Legal Address");
        assertThat(result.getCity()).isEqualTo("City");
    }

    @Test
    void whenMapToDto_givenNullCompany_shouldReturnNull() {
        // Act
        GetCompanyDto result = companyMapper.mapToDto(null);

        assertThat(result).isNull();
    }
}
