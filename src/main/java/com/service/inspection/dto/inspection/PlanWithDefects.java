package com.service.inspection.dto.inspection;

import lombok.Data;

import java.util.List;

@Data
public class PlanWithDefects {
    private List<PlanDefectDto> planDefectDtoList;
}
