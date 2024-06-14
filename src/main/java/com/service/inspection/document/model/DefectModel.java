package com.service.inspection.document.model;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DefectModel {

    @ToString.Include
    private String name;
}
