package com.service.inspection.dto.company;

import java.util.List;
import java.util.UUID;

import com.service.inspection.dto.employer.GetEmployerDto;
import com.service.inspection.dto.license.GetLicenseDto;

import lombok.Data;

@Data
public class GetCompanyDto {

    private Long id;
    private String name;
    private String legalAddress;
    private String city;
    private String logoName;
    private UUID logoUuid;
    private String sroScanName;
    private UUID sroScanUuid;
    private List<GetEmployerDto> employers;
    private List<GetLicenseDto> licenses;
}
