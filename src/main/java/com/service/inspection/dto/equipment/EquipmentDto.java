package com.service.inspection.dto.equipment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Data
@Validated
public class EquipmentDto {

    private String name;
    @NotBlank
    private String serialNumber;
    private String verificationNumber;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate verificationDate;

}
