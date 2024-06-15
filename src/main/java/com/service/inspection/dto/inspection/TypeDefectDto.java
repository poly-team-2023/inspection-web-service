package com.service.inspection.dto.inspection;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;


@Data
public class TypeDefectDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID uuid;

    private String name;

    @Size(min = 1, max = 10)
    private String hexCode;

    private Instant lastUpdateDate = Instant.now();
}
