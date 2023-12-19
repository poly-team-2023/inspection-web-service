package com.service.inspection.dto.files;

import com.service.inspection.dto.NamedDto;
import lombok.Data;

@Data
public class GetFileScanDto extends NamedDto {

    private int scanNumber;
}
