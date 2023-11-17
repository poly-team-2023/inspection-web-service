package com.service.inspection.mapper;

import com.service.inspection.entities.Category;
import com.service.inspection.entities.Inspection;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"
)
public interface CategoryMapper {
    Category mapToCategory(String name, Inspection inspection);
}
