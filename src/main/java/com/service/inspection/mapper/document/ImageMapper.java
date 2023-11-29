package com.service.inspection.mapper.document;

import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.PictureType;
import com.deepoove.poi.data.Pictures;
import com.service.inspection.configs.DocumentEngineConfig;
import com.service.inspection.document.model.ImageModel;
import com.service.inspection.entities.Photo;
import com.service.inspection.service.StorageService;
import com.service.inspection.service.document.ProcessingImageDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"
)
@Slf4j
public abstract class ImageMapper {

    @Autowired
    private StorageService storageService;

    @Autowired
    private DocumentEngineConfig documentEngineConfig;

    @Named(value = "mapToModelPicture")
    public PictureRenderData mapToImageModel(byte[] bytes) {
        return Pictures.ofBytes(bytes, PictureType.JPEG).create();
    }

    @Mapping(source = "photoBytes", target = "image", qualifiedByName = "mapToModelPicture")
    @Mapping(source = "defects", target = "imageTitle")
    public abstract ImageModel mapToImageModel(ProcessingImageDto processingImageDto);

    public String mapToImageTitle(Set<Photo.Defect> defects) {
        if (defects != null) {
            return Strings.join(defects.stream().map(Photo.Defect::getName).toList(), ' ');
        }
        return "Без названия";
    }
}
