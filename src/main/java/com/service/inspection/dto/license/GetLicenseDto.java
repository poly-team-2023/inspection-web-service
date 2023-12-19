package com.service.inspection.dto.license;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.service.inspection.dto.NamedDto;
import com.service.inspection.dto.files.GetFileScanDto;
import lombok.Data;

import java.util.List;

@Data
public class GetLicenseDto extends NamedDto {

    @JsonProperty(value = "files")
    private List<GetFileScanDto> files;

}
