package com.service.inspection.dto.inspection;

import com.service.inspection.dto.NamedDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InspectionPlansDto {
    private List<NamedDto> plans;
}
