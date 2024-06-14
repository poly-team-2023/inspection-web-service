package com.service.inspection.mapper;

import com.service.inspection.dto.employer.EmployerDto;
import com.service.inspection.entities.Employer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmployerMapperImpl.class})
class EmployerMapperTest {

    @Autowired
    private EmployerMapper employerMapper;


    @Test
    void whenMapToEmployer_givenNameAndPositionName_shouldReturnEmployer() {
        // Arrange
        String name = "John Doe";
        String positionName = "Manager";

        // Act
        Employer result = employerMapper.mapToEmployer(name, positionName);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getPositionName()).isEqualTo(positionName);
    }

    @Test
    void whenMapToEmployer_givenNullNameAndPositionName_shouldReturnNull() {
        // Act
        Employer result = employerMapper.mapToEmployer(null, null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void whenMapToEmployerDto_givenNameAndPositionName_shouldReturnEmployerDto() {
        // Arrange
        String name = "Jane Smith";
        String positionName = "Developer";

        // Act
        EmployerDto result = employerMapper.mapToEmployerDto(name, positionName);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getPositionName()).isEqualTo(positionName);
    }

    @Test
    void whenMapToEmployerDto_givenNullNameAndPositionName_shouldReturnNull() {
        // Act
        EmployerDto result = employerMapper.mapToEmployerDto(null, null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void whenMapToUpdateEmployer_givenToUpdateAndSource_shouldUpdateEmployer() {
        // Arrange
        Employer toUpdate = new Employer();
        EmployerDto source = new EmployerDto();
        source.setName("Updated Name");
        source.setPositionName("Updated Position");

        // Act
        employerMapper.mapToUpdateEmployer(toUpdate, source);

        // Assert
        assertThat(toUpdate.getName()).isEqualTo("Updated Name");
        assertThat(toUpdate.getPositionName()).isEqualTo("Updated Position");
    }

    @Test
    void whenMapToUpdateEmployer_givenNullSource_shouldNotUpdateEmployer() {
        // Arrange
        Employer toUpdate = new Employer();
        toUpdate.setName("Original Name");
        toUpdate.setPositionName("Original Position");

        // Act
        employerMapper.mapToUpdateEmployer(toUpdate, null);

        // Assert
        assertThat(toUpdate.getName()).isEqualTo("Original Name");
        assertThat(toUpdate.getPositionName()).isEqualTo("Original Position");
    }
}
