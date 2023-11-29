package com.service.inspection.dto.company;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Validated
public class CompanyDto {

    @NotBlank
    private String name;
    @NotBlank
    private String legalAddress;
    private String city;

}
