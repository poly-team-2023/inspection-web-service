package com.service.inspection.mapper.document;

import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.PictureType;
import com.deepoove.poi.data.Pictures;
import com.service.inspection.configs.BucketName;
import com.service.inspection.configs.DocumentEngineConfig;
import com.service.inspection.document.model.ImageModel;
import com.service.inspection.entities.FileEntity;
import com.service.inspection.entities.Photo;
import com.service.inspection.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"

)
@Slf4j
abstract class ImageMapper {

    @Autowired
    private StorageService storageService;

    @Autowired
    private DocumentEngineConfig documentEngineConfig;

    @Named(value = "mapToModelPicture")
    public PictureRenderData mapToImageModel(byte[] bytes) {
        return Pictures.ofBytes(bytes, PictureType.JPEG).create();
    }
}
