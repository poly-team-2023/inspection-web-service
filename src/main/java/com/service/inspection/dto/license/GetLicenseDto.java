package com.service.inspection.dto.license;

import lombok.Data;

import java.util.UUID;

@Data
public class GetLicenseDto {

    private Long id;
    private String name;
    private UUID uuid;
    private Integer number;

}
