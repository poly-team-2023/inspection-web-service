package com.service.inspection.dto.equipment;

import java.time.LocalDate;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Validated
public class EquipmentDto {

    private String name;
    @NotBlank
    private String serialNumber;
    @NotNull
    private LocalDate verificationDate;

}
