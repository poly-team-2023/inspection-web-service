package com.service.inspection.mapper.document;

import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.PictureType;
import com.deepoove.poi.data.Pictures;
import com.service.inspection.document.model.ImageModel;
import com.service.inspection.entities.Photo;
import com.service.inspection.service.document.ProcessingImageDto;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"
)
@Slf4j
public abstract class ImageMapper {

    @Named(value = "mapToModelPicture")
    public PictureRenderData mapToImageModel(byte[] bytes) {
        return Pictures.ofBytes(bytes, PictureType.JPEG).create();
    }

    @Mapping(source = "photoBytes", target = "image", qualifiedByName = "mapToModelPicture")
    @Mapping(source = "defects", target = "imageTitle")
    public abstract ImageModel mapToImageModel(ProcessingImageDto processingImageDto);

    public String mapToImageTitle(Set<Photo.Defect> defects) {
        if (defects != null) {
            return defects.stream().map(Photo.Defect::getName).collect(Collectors.joining(", "));
        }
        return "Без названия";
    }
}
