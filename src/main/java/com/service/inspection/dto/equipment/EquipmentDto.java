package com.service.inspection.dto.equipment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;

@Data
@Validated
public class EquipmentDto {

    @NotBlank
    private String name;
    @NotBlank
    private String serialNumber;
    @NotBlank
    private String verificationScanName;
    @NotNull
    private OffsetDateTime verificationDate;

}
