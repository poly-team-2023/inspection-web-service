package com.service.inspection.dto.company;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.service.inspection.dto.NamedDto;
import com.service.inspection.dto.employer.GetEmployerDto;
import com.service.inspection.dto.files.GetFileScanDto;
import com.service.inspection.dto.license.GetLicenseDto;

import lombok.Data;

@Data
public class GetCompanyDto extends NamedDto {

    private String legalAddress;
    private String city;
    @JsonProperty(value = "sro")
    private List<GetFileScanDto> filesSro;
    private List<GetEmployerDto> employers;
    private List<GetLicenseDto> licenses;
}
