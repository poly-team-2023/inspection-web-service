package com.service.inspection.mapper;

import com.service.inspection.dto.license.LicenseDto;
import com.service.inspection.entities.License;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LicenseMapperImpl.class})
class LicenseMapperTest {

    @Autowired
    private LicenseMapper licenseMapper;

    @Test
    void whenMapToLicense_givenLicenseDto_shouldReturnLicense() {
        // Arrange
        LicenseDto dto = new LicenseDto();
        dto.setName("TestLicense");

        // Act
        License result = licenseMapper.mapToLicense(dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("TestLicense");
    }

    @Test
    void whenMapToLicense_givenNullLicenseDto_shouldReturnNull() {
        // Act
        License result = licenseMapper.mapToLicense(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void whenMapToUpdateLicense_givenSourceLicenseDto_shouldUpdateLicense() {
        // Arrange
        License toUpdate = new License();
        LicenseDto source = new LicenseDto();
        source.setName("UpdatedLicense");

        // Act
        licenseMapper.mapToUpdateLicense(toUpdate, source);

        // Assert
        assertThat(toUpdate.getName()).isEqualTo("UpdatedLicense");
    }

    @Test
    void whenMapToUpdateLicense_givenNullSourceLicenseDto_shouldNotUpdateLicense() {
        // Arrange
        License toUpdate = new License();
        toUpdate.setName("OriginalLicense");

        // Act
        licenseMapper.mapToUpdateLicense(toUpdate, null);

        // Assert
        assertThat(toUpdate.getName()).isEqualTo("OriginalLicense");
    }
}
