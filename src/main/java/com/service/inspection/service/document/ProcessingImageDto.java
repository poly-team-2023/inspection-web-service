package com.service.inspection.service.document;

import com.service.inspection.entities.Photo;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class ProcessingImageDto {
    private Long id;
    private Long photoNum;

    private UUID uuid;
    private byte[] photoBytes;
    private Set<Photo.Defect> defects;
}
