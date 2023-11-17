package com.service.inspection.dto.equipment;

import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class GetEquipmentDto {

    private Long id;
    private String name;
    private String serialNumber;
    private String verificationScanName;
    private LocalDate verificationDate;
    private UUID verificationScanUuid;
}
