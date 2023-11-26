package com.service.inspection.service;

import com.service.inspection.document.model.ImageModel;
import com.service.inspection.entities.Photo;
import com.service.inspection.mapper.PhotoMapper;
import com.service.inspection.mapper.document.DocumentMapper;
import com.service.inspection.repositories.InspectionRepository;
import com.service.inspection.service.document.ImageProcessingFacade;
import com.service.inspection.service.document.ProcessingImageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentModelService {

    private final StorageService storageService;
    private final ImageProcessingFacade imageProcessingFacade;
    private final InspectionRepository inspectionRepository;
    private final PhotoMapper photoMapper;
    private final DocumentMapper documentMapper;

    @Async
    public CompletableFuture<List<ImageModel>> processAllPhotosAsync(Set<Photo> photos) {
        log.info("Start all photos processing" + Thread.currentThread().getName());

        List<CompletableFuture<ImageModel>> futures = Collections.synchronizedList(new ArrayList<>());
        for (Photo photo: photos) {
            ProcessingImageDto processingImageDto = photoMapper.mapToProcessingImage(photo);
            futures.add(imageProcessingFacade.processPhoto(processingImageDto)
                    .thenApplyAsync(documentMapper::mapToImageModel));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        log.info("End all photos document processing" + Thread.currentThread().getName());

        return CompletableFuture
                .completedFuture(futures.stream().map(x -> x.getNow(null)).collect(Collectors.toList()));
    }

    public static <T> CompletableFuture<List<T>> all(List<CompletableFuture<T>> futures) {
        CompletableFuture[] cfs = futures.toArray(new CompletableFuture[futures.size()]);

        return CompletableFuture.allOf(cfs)
                .thenApply(ignored -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
                );
    }
}
