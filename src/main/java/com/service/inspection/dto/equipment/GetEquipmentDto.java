package com.service.inspection.dto.equipment;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class GetEquipmentDto {

    private Long id;
    private String name;
    private String serialNumber;
    private String verificationScanName;
    private LocalDate verificationDate;
    private UUID verificationScanUuid;
}
