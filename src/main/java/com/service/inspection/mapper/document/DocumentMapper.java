package com.service.inspection.mapper.document;

import com.service.inspection.document.DocumentModel;
import com.service.inspection.document.model.CategoryModel;
import com.service.inspection.document.model.CompanyModel;
import com.service.inspection.document.model.ImageModel;
import com.service.inspection.entities.Category;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.Inspection;
import com.service.inspection.entities.Photo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
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
    @Mapping(source = "mainPhotoUuid", target = "mainPhoto", qualifiedByName = "getPhotoSyncByUuid")
    public abstract DocumentModel mapToDocumentModel(Inspection inspection);

    @Mapping(source = "photos", target = "photos")
    abstract CategoryModel mapToCategoryModel(Category category);

    abstract CompanyModel mapToCompanyModel(Company company);

    List<ImageModel> mapToImagesPhotos(Set<Photo> set) {
        List<CompletableFuture<ImageModel>> futureResult = new ArrayList<>();
        for (Photo photo: set) {
            futureResult.add(imageMapper.mapToImageModel(photo));
        }

        CompletableFuture.allOf(futureResult.toArray(new CompletableFuture[0])).join();

        return futureResult.stream()
                .map(x -> x.getNow(null))
                .collect(Collectors.toList());
    }
}
