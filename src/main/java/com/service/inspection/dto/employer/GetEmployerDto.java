package com.service.inspection.dto.employer;

import lombok.Data;

import java.util.UUID;

@Data
public class GetEmployerDto {

    private Long id;
    private String name;
    private String positionName;
    private String signatureName;
    private UUID signatureUuid;
}
