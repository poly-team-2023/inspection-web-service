package com.service.inspection.dto.inspection;

import lombok.Data;

import java.util.List;

@Data
public class InspectionWithTypeDefect {
    private List<TypeDefectDto> typeDefects;
}
