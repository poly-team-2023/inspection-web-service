package com.service.inspection.dto.employer;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Validated
public class EmployerDto {

    @NotBlank
    private String name;
    @NotBlank
    private String positionName;

}
