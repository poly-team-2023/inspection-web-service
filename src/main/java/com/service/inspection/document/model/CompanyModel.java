package com.service.inspection.document.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyModel {
    private String name;
    private String legalAddress;
    private String city;
    private ImageModel logo;
}
