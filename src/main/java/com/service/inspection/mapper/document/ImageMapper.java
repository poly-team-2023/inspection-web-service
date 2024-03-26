package com.service.inspection.mapper.document;

import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.PictureType;
import com.deepoove.poi.data.Pictures;
import com.service.inspection.document.model.ImageModel;
import com.service.inspection.document.model.ImageModelWithDefects;
import com.service.inspection.dto.document.PhotoDefectsDto;
import com.service.inspection.entities.Photo;
import com.service.inspection.service.document.ProcessingImageDto;
import com.service.inspection.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"
)
@Slf4j
public abstract class ImageMapper {

    @Autowired
    public CommonUtils utils;

    @Named(value = "mapToModelPicture")
    public PictureRenderData mapToImageModel(byte[] bytes) {
        return Pictures.ofBytes(bytes, PictureType.JPEG).create();
    }

    @Mapping(source = "photoBytes", target = "image", qualifiedByName = "mapToModelPicture")
    @Mapping(source = "id", target = "imageTitle", defaultValue = "Без названия")
    public abstract ImageModel mapToImageModel(ProcessingImageDto processingImageDto);

    @Mapping(source = "photoBytes", target = "image", qualifiedByName = "mapToModelPicture")
    @Mapping(source = "photoDefectsDto.defectsDto", target = "imageTitle", defaultValue = "Дефектов не выявлено")
    @Mapping(source = "photoDefectsDto.defectsDto", target = "defects")
    public abstract ImageModelWithDefects mapToImageModelWithDefects(ProcessingImageDto processingImageDto);


    public String mapToImageTitle(Set<PhotoDefectsDto.DefectDto> defects) {
        if (defects.isEmpty()) return "Дефектов не выявлено";
        return utils.toHumanReadable(defects.stream().map(PhotoDefectsDto.DefectDto::getDefectName)
                .collect(Collectors.joining(", ")));
    }
}
