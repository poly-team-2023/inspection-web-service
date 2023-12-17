package com.service.inspection.dto.equipment;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class GetEquipmentDto {

    private Long id;
    private String name;
    private String serialNumber;
    private String verificationNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate verificationDate;

}
