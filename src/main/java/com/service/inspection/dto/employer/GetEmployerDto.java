package com.service.inspection.dto.employer;

import java.util.UUID;

import lombok.Data;

@Data
public class GetEmployerDto {

    private Long id;
    private String name;
    private String positionName;
    private String signatureName;
    private UUID signatureUuid;
}
