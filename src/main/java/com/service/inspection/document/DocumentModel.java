package com.service.inspection.document;

import com.service.inspection.document.model.CategoryModel;
import com.service.inspection.document.model.CompanyModel;
import com.service.inspection.document.model.ImageModel;
import lombok.Builder;

import java.util.List;

@Builder
public class DocumentModel {
    private String projectName;
    private String reportName = "Технический отчет об обследовании";
    private String script;

    private ImageModel mainPhoto;

    private CompanyModel company;
    private List<CategoryModel> categories;
}
