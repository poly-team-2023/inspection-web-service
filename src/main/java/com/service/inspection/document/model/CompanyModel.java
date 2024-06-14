package com.service.inspection.document.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class CompanyModel {
    private String name;
    private String legalAddress;
    private String city;
    private ImageModel logo;

    private List<ImageModel> files = Collections.synchronizedList(new ArrayList<>());
}
