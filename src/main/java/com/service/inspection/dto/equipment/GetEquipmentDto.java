package com.service.inspection.dto.equipment;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GetEquipmentDto {

    private Long id;
    private String name;
    private String serialNumber;;
    private LocalDate verificationDate;

}
