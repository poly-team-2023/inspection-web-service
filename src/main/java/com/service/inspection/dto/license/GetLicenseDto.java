package com.service.inspection.dto.license;

import java.util.UUID;

import lombok.Data;

@Data
public class GetLicenseDto {

    private Long id;
    private String name;
    private UUID uuid;
    private Integer number;

}
