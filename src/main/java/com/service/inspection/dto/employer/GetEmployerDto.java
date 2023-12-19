package com.service.inspection.dto.employer;

import com.service.inspection.dto.NamedDto;
import lombok.Data;

@Data
public class GetEmployerDto extends NamedDto {

    private String positionName;
    private String signatureName;

}
