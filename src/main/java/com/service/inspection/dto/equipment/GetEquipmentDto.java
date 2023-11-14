package com.service.inspection.dto.equipment;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class GetEquipmentDto {

    private Long id;
    private String name;
    private String serialNumber;
    private String verificationScanName;
    private OffsetDateTime verificationDate;
    private UUID verificationScanUuid;
}
