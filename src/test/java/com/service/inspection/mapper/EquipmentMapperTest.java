package com.service.inspection.mapper;

import com.service.inspection.dto.equipment.EquipmentDto;
import com.service.inspection.dto.equipment.GetEquipmentDto;
import com.service.inspection.entities.Equipment;
import com.service.inspection.entities.FileScan;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EquipmentMapperImpl.class})
class EquipmentMapperTest {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Test
    void whenMapToEquipment_givenEquipmentDto_shouldReturnEquipment() {
        // Arrange
        EquipmentDto dto = new EquipmentDto();
        dto.setName("TestEquipment");
        dto.setSerialNumber("123456");
        dto.setVerificationNumber("ABC123");
        dto.setVerificationDate(LocalDate.now());

        // Act
        Equipment result = equipmentMapper.mapToEquipment(dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("TestEquipment");
        assertThat(result.getSerialNumber()).isEqualTo("123456");
        assertThat(result.getVerificationNumber()).isEqualTo("ABC123");
        assertThat(result.getVerificationDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void whenMapToEquipment_givenNullEquipmentDto_shouldReturnNull() {
        // Act
        Equipment result = equipmentMapper.mapToEquipment(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void whenMapToDto_givenEquipment_shouldReturnGetEquipmentDto() {
        // Arrange
        Set<FileScan> fileScans = new HashSet<>();
        FileScan fileScan = new FileScan();
        fileScan.setId(1L);
        fileScan.setName("TestFileScan");
        fileScan.setScanNumber(123);
        fileScans.add(fileScan);


        // Arrange
        Equipment equipment = new Equipment();
        equipment.setId(1L);
        equipment.setName("TestEquipment");
        equipment.setSerialNumber("123456");
        equipment.setVerificationNumber("ABC123");
        equipment.setVerificationDate(LocalDate.now());
        equipment.setFiles(fileScans); // Assume this method exists and returns a Set of FileScan objects

        // Act
        GetEquipmentDto result = equipmentMapper.mapToDto(equipment);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("TestEquipment");
        assertThat(result.getSerialNumber()).isEqualTo("123456");
        assertThat(result.getVerificationNumber()).isEqualTo("ABC123");
        assertThat(result.getVerificationDate()).isEqualTo(LocalDate.now());
        assertThat(result.getFiles()).extracting("id", "scanNumber", "name")
                .containsExactlyInAnyOrder(Tuple.tuple(1L, 123, "TestFileScan"));
    }

    @Test
    void whenMapToDto_givenNullEquipment_shouldReturnNull() {
        // Act
        GetEquipmentDto result = equipmentMapper.mapToDto(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void whenMapToUpdateEquipment_givenToUpdateAndSource_shouldUpdateEquipment() {
        // Arrange
        Equipment toUpdate = new Equipment();
        EquipmentDto source = new EquipmentDto();
        source.setName("UpdatedEquipment");
        source.setSerialNumber("654321");
        source.setVerificationNumber("CBA321");
        source.setVerificationDate(LocalDate.now().minusDays(1));

        // Act
        equipmentMapper.mapToUpdateEquipment(toUpdate, source);

        // Assert
        assertThat(toUpdate.getName()).isEqualTo("UpdatedEquipment");
        assertThat(toUpdate.getSerialNumber()).isEqualTo("654321");
        assertThat(toUpdate.getVerificationNumber()).isEqualTo("CBA321");
        assertThat(toUpdate.getVerificationDate()).isEqualTo(LocalDate.now().minusDays(1));
    }

    @Test
    void whenMapToUpdateEquipment_givenNullSource_shouldNotUpdateEquipment() {
        // Arrange
        Equipment toUpdate = new Equipment();
        toUpdate.setName("OriginalEquipment");

        // Act
        equipmentMapper.mapToUpdateEquipment(toUpdate, null);

        // Assert
        assertThat(toUpdate.getName()).isEqualTo("OriginalEquipment");
        // Other properties are not asserted as they were not set initially
    }
}
