package com.service.inspection.dto.equipment;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class GetEquipmentDto {

    private Long id;
    private String name;
    private String serialNumber;
    private String verificationNumber;
    @JsonFormat(pattern = "dd.MM.yyyy")
    @DateTimeFormat(pattern = "dd.MM.yyy")
    private LocalDate verificationDate;

}
