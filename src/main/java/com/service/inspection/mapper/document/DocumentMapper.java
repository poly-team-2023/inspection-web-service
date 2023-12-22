package com.service.inspection.mapper.document;

import com.service.inspection.document.DocumentModel;
import com.service.inspection.document.model.CategoryModel;
import com.service.inspection.document.model.CompanyModel;
import com.service.inspection.document.model.ImageModelWithDefects;
import com.service.inspection.entities.Category;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.Employer;
import com.service.inspection.entities.Inspection;
import com.service.inspection.entities.User;
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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    @Mapping(source = "inspection.employer", target = "employer")
    public abstract DocumentModel mapToDocumentModel(Inspection inspection, User user, @Context List<CompletableFuture<Void>> futureResult);

    @Mapping(source = "processedPhotos", target = "photos")
    @Mapping(source = "category.id", target = "categoryNum", defaultValue = "0L")
    public abstract CategoryModel mapToCategoryModel(Category category, List<ImageModelWithDefects> processedPhotos);

    @AfterMapping
    public void mappingAsyncPhotoFields(@MappingTarget DocumentModel documentModel, Inspection inspection, User user,
                                        @Context List<CompletableFuture<Void>> futureResult) {
        List<Category> categories = inspection.getCategories();
        categories.sort(Comparator.comparingLong(Category::getId));

        Long lastPhotosCount = 1L;
        for (Category category : categories) {
            Hibernate.initialize(category.getPhotos());
            futureResult.add(
                    documentModelService.processAllPhotosAsync(category.getPhotos(), lastPhotosCount).thenAccept(x -> {

                        // TODO чтобы в модель фото на основе порядкового номера номера фото и категории
                        //  вставлялся ее номер

                        documentModel.addCategory(this.mapToCategoryModel(category, x));
                        log.debug("Add category {} to inspection {}", category.getId(), inspection.getId());
                    })
            );
            lastPhotosCount += category.getPhotos().size();
        }

        UUID uuid = inspection.getMainPhotoUuid();
        if (uuid != null) {
            futureResult.add(documentModelService.processAbstractPhoto(uuid).thenAccept(documentModel::setMainPhoto));
        }

        Optional.ofNullable(inspection.getCompany()).map(Company::getLogoUuid).ifPresent(logoUuid -> {
            futureResult.add(documentModelService.processAbstractPhoto(logoUuid).thenAccept(x -> {
                documentModel.getCompany().setLogo(x);
            }));
        });

        Optional.ofNullable(inspection.getEmployer()).map(Employer::getSignatureUuid).ifPresent(signUuid -> {
            futureResult.add(documentModelService.processAbstractPhoto(signUuid).thenAccept(x -> {
                documentModel.getEmployer().setScript(x);
            }));
        });
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
