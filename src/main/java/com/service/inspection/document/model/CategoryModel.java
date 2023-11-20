package com.service.inspection.document.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryModel {
    private String name;
    private List<ImageModel> photos;
}
