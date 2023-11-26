package com.service.inspection.document;

import com.service.inspection.document.model.CategoryModel;
import com.service.inspection.document.model.CompanyModel;
import com.service.inspection.document.model.ImageModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Builder
@Getter
@Setter
public class DocumentModel {
    private String projectName;
    private String reportName = "Технический отчет об обследовании";
    private String script;

    private ImageModel mainPhoto;

    private CompanyModel company;
    private List<CategoryModel> categories = Collections.synchronizedList(new ArrayList<CategoryModel>());

    public void addCategory(CategoryModel categoryModel) {
        categories.add(categoryModel);
    }
}
