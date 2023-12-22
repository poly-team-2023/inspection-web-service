package com.service.inspection.document.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ImageModelWithDefects extends ImageModel {
    private Long photoNum;
    private List<DefectModel> defects;
}
