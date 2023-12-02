package com.service.inspection.dto.inspection;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InspectionDto {
    @NotBlank
    private String name;

    @Min(1)
    private Long companyId;

    @Min(1)
    private Long employerId;

    private String address;

    private String script;

    private Boolean isCulture;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate startDate; // TODO {@link Inspection}

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate endDate;
}
