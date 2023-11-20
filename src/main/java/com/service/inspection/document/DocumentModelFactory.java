package com.service.inspection.document;

import com.service.inspection.entities.*;
import com.service.inspection.service.DataService;
import com.service.inspection.service.DocumentModelService;
import com.service.inspection.service.DocumentService;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DocumentModelFactory {

    private final DocumentService documentService;
    private final DataService dataService;

    private DocumentModelBuilder createBuilder(Inspection inspection) {
        DocumentModelBuilder documentModelBuilder = new DocumentModelBuilder();

        documentModelBuilder.setInspection(inspection);

        Collection<Category> categories = inspection.getCategories();
        if (categories != null) {
            documentModelBuilder.setCategory(inspection.getCategories());
            Collection<Photo> photos = new ArrayList<>();
            for (Category category : categories) {
                Optional.ofNullable(category.getPhotos()).ifPresent(photos::addAll);
            }
        }
        return null;
    }

    @Getter
    @Setter
    private class DocumentModelBuilder {
        private Inspection inspection;
        private Collection<Category> category;
        private Collection<Photo> photo;
        private Employer employer;
        private Company company;
    }
}
