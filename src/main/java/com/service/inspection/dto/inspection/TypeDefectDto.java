package com.service.inspection.dto.inspection;

import com.service.inspection.dto.NamedDto;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TypeDefectDto extends NamedDto {
    private String name;

    @Size(min = 1, max = 10)
    private String hexCode;
}
