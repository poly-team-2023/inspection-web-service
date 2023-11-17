package com.service.inspection.dto.equipment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;

@Data
@Validated
public class EquipmentDto {

    private String name;
    @NotBlank
    private String serialNumber;
    @NotNull
    private OffsetDateTime verificationDate;

}
