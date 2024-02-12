package com.service.inspection.mapper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.service.inspection.document.DocumentModel;
import com.service.inspection.document.model.CategoryModel;
import com.service.inspection.document.model.DefectModel;
import com.service.inspection.document.model.ImageModelWithDefects;
import com.service.inspection.dto.document.GptReceiverDto;
import com.service.inspection.dto.document.GptSenderDto;
import com.service.inspection.entities.Category;
import com.service.inspection.mapper.document.TableMapper;
import org.aspectj.lang.annotation.After;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"
)
public abstract class SenderMapper {

    @Autowired
    private TableMapper tableMapper;

    @Mapping(source = "building.recommendation", target = "recommendation")
    @Mapping(source = "building.estimation", target = "estimation")
    @Mapping(source = "building.categories", target = "categories")
    public abstract void updateDocumentModelWithGpt(@MappingTarget DocumentModel documentModel, GptReceiverDto receiver);

    void updateDocumentModelWithGptBuilding(@MappingTarget List<CategoryModel> categoriesModel,
                                            List<GptReceiverDto.GptReceiverCategoryDto> receiverCategoryDto) {
        if (receiverCategoryDto == null) return;

        Map<Long, CategoryModel> map = Maps.uniqueIndex(categoriesModel, CategoryModel::getId);

        for (GptReceiverDto.GptReceiverCategoryDto categoryDto: receiverCategoryDto) {
            CategoryModel categoryToModify = map.getOrDefault(categoryDto.getId(), null);

            if (categoryToModify == null) {
                continue;
            }

            categoryToModify.setEstimation(categoryDto.getEstimation());
        }
    }

    @AfterMapping
    void renderTableRender(@MappingTarget DocumentModel documentModel, GptReceiverDto receiver) {
        documentModel.setCategoriesDefectsTable(tableMapper.createSumDefectsTable(documentModel.getCategories()));
    }

    // ------------------ преобразование модели после обработки всех фотографий во всех категориях -----------------

    @Mapping(source = "categories", target = "categories")
    public abstract GptSenderDto mapToGptSenderDto(DocumentModel model);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "photos", target = "defects")
    abstract GptSenderDto.GptCategoryDto mapToGptCategoryDto(CategoryModel categoryModel);


    List<GptSenderDto.GptDefectDto> mapToGptCategoryDto(List<ImageModelWithDefects> defects) {

        Map<String, Integer> mapDefects = defects.stream().flatMap(t -> t.getDefects().stream())
                                .collect(Collectors.toMap(DefectModel::getName, v -> 1, Integer::sum));

        List<GptSenderDto.GptDefectDto> answer = new ArrayList<>();
        mapDefects.forEach((key, value) -> answer.add(new GptSenderDto.GptDefectDto(key, value.longValue())));
        return answer;
    }


}
