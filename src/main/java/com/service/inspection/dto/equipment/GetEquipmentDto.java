package com.service.inspection.dto.equipment;

import java.time.LocalDate;

import lombok.Data;

@Data
public class GetEquipmentDto {

    private Long id;
    private String name;
    private String serialNumber;
    private LocalDate verificationDate;

}
