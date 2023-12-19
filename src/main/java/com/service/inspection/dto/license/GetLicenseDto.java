package com.service.inspection.dto.license;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.service.inspection.dto.files.GetFileScanDto;
import lombok.Data;

import java.util.List;

@Data
public class GetLicenseDto {

    private Long id;
    private String name;
    @JsonProperty(value = "files")
    private List<GetFileScanDto> files;

}
