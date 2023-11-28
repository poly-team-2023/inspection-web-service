package com.service.inspection.mapper;

import java.util.Set;

import com.service.inspection.dto.inspection.CategoryWithFile;
import com.service.inspection.entities.Category;
import com.service.inspection.entities.Inspection;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring",
        uses = {CommonMapper.class, PhotoMapper.class}

)
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "inspection", target = "inspection")
    @Mapping(source = "name", target = "name")
    Category mapToCategory(String name, Inspection inspection);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "name", target = "name")
    void mapToCategory(@MappingTarget Category category, String name);


    Set<CategoryWithFile> mapToCategoryWithFile(Set<Category> categories);
}
