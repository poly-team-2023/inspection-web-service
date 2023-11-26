package com.service.inspection.mapper.document;

import com.service.inspection.document.DocumentModel;
import com.service.inspection.document.model.CategoryModel;
import com.service.inspection.document.model.CompanyModel;
import com.service.inspection.document.model.ImageModel;
import com.service.inspection.entities.Category;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.Inspection;
import com.service.inspection.entities.Photo;
import com.service.inspection.service.document.ProcessingImageDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring",
        uses = {ImageMapper.class}

)
public abstract class DocumentMapper {

    @Autowired
    private ImageMapper imageMapper;

    @Mapping(target = "company", source = "company")
    @Mapping(source = "name", target = "projectName")
    @Mapping(source = "reportName", target = "reportName", defaultValue = "Технический отчет об обследовании")
    @Mapping(source = "script", target = "script")
//    @Mapping(source = "mainPhotoUuid", target = "mainPhoto", qualifiedByName = {"getPhotoSyncByUuid"})
    @Mapping(target = "categories", ignore = true)
    public abstract DocumentModel mapToDocumentModel(Inspection inspection);

    @Mapping(source = "processedPhotos", target = "photos")
    public abstract CategoryModel mapToCategoryModel(Category category, Collection<ImageModel> processedPhotos);

//    @Mapping(source = "logoUuid", target = "logo", qualifiedByName = {"getPhotoSyncByUuid"})
//    abstract CompanyModel mapToCompanyModel(Company company);

    @Mapping(source = "photoBytes", target = "image", qualifiedByName = "mapToModelPicture")
    public abstract ImageModel mapToImageModel(ProcessingImageDto processingImageDto);

//    @Mapping(source = "images", target = "model.categories.photos")
//    public abstract void addProcessedPhotos(@MappingTarget DocumentModel model, List<ImageModel> images);
}
