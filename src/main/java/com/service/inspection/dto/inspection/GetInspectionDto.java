package com.service.inspection.dto.inspection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.service.inspection.dto.NamedDto;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GetInspectionDto extends NamedDto {
    private String address;
    private String script;
    private Boolean isCulture;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate endDate;

    private NamedDto company;
    private NamedDto employer;
}
