package com.service.inspection.dto.document;

import com.service.inspection.entities.enums.ProgressingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentStatusDto {
    private ProgressingStatus progressingStatus;
}
