package com.service.inspection.document;

import com.deepoove.poi.data.TableRenderData;
import com.service.inspection.document.model.CategoryModel;
import com.service.inspection.document.model.CompanyModel;
import com.service.inspection.document.model.EmployerModel;
import com.service.inspection.document.model.ImageModel;
import lombok.Getter;
import lombok.Setter;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class DocumentModel {
    private Integer year = Year.now().getValue();
    private String projectName;
    private String reportName;
    private String script;

    private EmployerModel employer;

    private ImageModel mainPhoto;

    private CompanyModel company;

    private TableRenderData equipment;

    private List<CategoryModel> categories = Collections.synchronizedList(new ArrayList<>());

    public void addCategory(CategoryModel categoryModel) {
        categories.add(categoryModel);
    }

    private String recommendation;
    private String estimation;
}
