package com.service.inspection.service;

import com.service.inspection.document.model.ImageModel;
import com.service.inspection.document.model.ImageModelWithDefects;
import com.service.inspection.entities.Photo;
import com.service.inspection.mapper.PhotoMapper;
import com.service.inspection.mapper.document.ImageMapper;
import com.service.inspection.service.document.ImageProcessingFacade;
import com.service.inspection.service.document.ProcessingImageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentModelService {

    private final ImageProcessingFacade imageProcessingFacade;
    private final PhotoMapper photoMapper;
    private final ImageMapper imageMapper;

    @Async("categoryAsyncExecutor")
    public CompletableFuture<List<ImageModelWithDefects>> processAllPhotosAsync(List<Photo> photos, Long startNum) {
        photos.sort(Comparator.comparingLong(Photo::getId)); // TODO нормальное реализовать

        log.debug("Start all photos processing {}", Thread.currentThread().getName());
        List<CompletableFuture<ImageModelWithDefects>> futures = Collections.synchronizedList(new ArrayList<>());
        for (Photo photo : photos) {
            ProcessingImageDto processingImageDto = photoMapper.mapToProcessingImage(photo, startNum++);
            futures.add(imageProcessingFacade.processPhoto(processingImageDto).thenApply(imageMapper::mapToImageModelWithDefects));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenApply(f -> {
            log.debug("End all photos processing {}", Thread.currentThread().getName());
            return futures.stream().map(CompletableFuture::join).toList();
        });
    }

    public CompletableFuture<ImageModel> processAbstractPhoto(UUID uuid) {
        ProcessingImageDto processingImageDto = photoMapper.mapToProcessingImage(uuid);
        return imageProcessingFacade.processPhoto(processingImageDto).thenApply(imageMapper::mapToImageModel);
    }
}
