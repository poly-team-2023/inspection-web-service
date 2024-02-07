package com.service.inspection.document.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DefectModel {

    @ToString.Include
    private String name;
}
