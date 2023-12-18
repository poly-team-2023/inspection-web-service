package com.service.inspection.mapper.document;

import com.service.inspection.document.DocumentModel;
import com.service.inspection.document.model.CategoryModel;
import com.service.inspection.document.model.CompanyModel;
import com.service.inspection.document.model.ImageModel;
import com.service.inspection.entities.*;
import com.service.inspection.service.DocumentModelService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring",
        uses = {ImageMapper.class, TableMapper.class}
)
@Slf4j
public abstract class DocumentMapper {

    @Autowired
    private DocumentModelService documentModelService;

    @Mapping(target = "company", source = "inspection.company")
    @Mapping(source = "inspection.name", target = "projectName")
    @Mapping(source = "inspection.reportName", target = "reportName", defaultValue = "Технический отчет об обследовании")
    @Mapping(source = "inspection.script", target = "script")
    @Mapping(target = "categories", ignore = true)
    public abstract DocumentModel mapToDocumentModel(Inspection inspection, User user, @Context List<CompletableFuture<Void>> futureResult);

    @Mapping(source = "processedPhotos", target = "photos")
    public abstract CategoryModel mapToCategoryModel(Category category, Collection<ImageModel> processedPhotos);

    @AfterMapping
    public void mappingAsyncPhotoFields(@MappingTarget DocumentModel documentModel, Inspection inspection, User user,
                                        @Context List<CompletableFuture<Void>> futureResult) {
        Set<Category> categories = inspection.getCategories();
        for (Category category : categories) {
            Hibernate.initialize(category.getPhotos());
            futureResult.add(
                    documentModelService.processAllPhotosAsync(category.getPhotos()).thenAccept(x -> {
                        documentModel.addCategory(this.mapToCategoryModel(category, x));
                        log.debug("Add category {} to inspection {}", category.getId(), inspection.getId());
                    })
            );
        }

        UUID uuid = inspection.getMainPhotoUuid();
        if (uuid != null) {
            futureResult.add(documentModelService.processAbstractPhoto(uuid).thenAccept(documentModel::setMainPhoto));
        }
    }

    abstract CompanyModel companyModel(Company company, @Context List<CompletableFuture<Void>> futureResult);

    @AfterMapping
    public void mappingCompanyPhotoAsync(@MappingTarget CompanyModel companyModel, Company company,
                                         @Context List<CompletableFuture<Void>> futureResult) {
        UUID uuid = company.getLogoUuid();
        if (uuid != null) {
            futureResult.add(documentModelService.processAbstractPhoto(uuid).thenAccept(companyModel::setLogo));
        }
    }
}
