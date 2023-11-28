package com.service.inspection.mapper;

import java.util.UUID;

import com.service.inspection.dto.inspection.CategoryWithFile;
import com.service.inspection.entities.Category;
import com.service.inspection.entities.Photo;
import com.service.inspection.entities.Plan;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"
)
public interface PhotoMapper {
    CategoryWithFile.PhotoDto mapToPhotoDto(Photo photo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recommendation", ignore = true)
    @Mapping(source = "plan", target = "plan")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "uuid", target = "fileUuid")
    Photo mapToPhoto(String name, UUID uuid, Category category, Plan plan);
}
