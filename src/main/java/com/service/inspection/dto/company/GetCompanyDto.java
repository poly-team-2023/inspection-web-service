package com.service.inspection.dto.company;

import java.util.List;

import com.service.inspection.dto.employer.GetEmployerDto;
import com.service.inspection.dto.license.GetLicenseDto;

import lombok.Data;

@Data
public class GetCompanyDto {

    private Long id;
    private String name;
    private String legalAddress;
    private String city;
    private List<GetEmployerDto> employers;
    private List<GetLicenseDto> licenses;
}
