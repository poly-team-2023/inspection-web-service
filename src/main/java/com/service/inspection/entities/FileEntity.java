package com.service.inspection.entities;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@MappedSuperclass
@Data
@NoArgsConstructor
public class FileEntity {
    private UUID fileUuid;

    private String fileName;
}
