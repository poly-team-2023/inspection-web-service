package com.service.inspection.dto.equipment;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.service.inspection.dto.files.GetFileScanDto;
import lombok.Data;

@Data
public class GetEquipmentDto {

    private Long id;
    private String name;
    private String serialNumber;
    private String verificationNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate verificationDate;
    private List<GetFileScanDto> files;

}
