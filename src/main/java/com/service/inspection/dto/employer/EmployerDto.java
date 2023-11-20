package com.service.inspection.dto.employer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class EmployerDto {

    @NotBlank
    private String name;
    @NotBlank
    private String positionName;

}
