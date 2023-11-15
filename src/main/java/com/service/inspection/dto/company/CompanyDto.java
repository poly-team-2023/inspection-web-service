package com.service.inspection.dto.company;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class CompanyDto {

    @NotBlank
    private String name;
    @NotBlank
    private String legalAddress;
    private String city;
    private String logoName;

}
