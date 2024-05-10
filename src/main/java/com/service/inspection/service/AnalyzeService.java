package com.service.inspection.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.inspection.document.DocumentModel;
import com.service.inspection.document.model.ImageModel;
import com.service.inspection.document.model.ImageModelWithDefects;
import com.service.inspection.dto.document.GptReceiverDto;
import com.service.inspection.dto.document.GptSenderDto;
import com.service.inspection.entities.Photo;
import com.service.inspection.mapper.PhotoMapper;
import com.service.inspection.mapper.SenderMapper;
import com.service.inspection.mapper.document.ImageMapper;
import com.service.inspection.service.document.ImageProcessingFacade;
import com.service.inspection.service.document.ProcessingImageDto;
import com.service.inspection.service.rabbit.RabbitMQService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyzeService {

    private final ImageProcessingFacade imageProcessingFacade;
    private final PhotoMapper photoMapper;
    private final ImageMapper imageMapper;
    private final RabbitMQService rabbitMQService;
    private final SenderMapper senderMapper;

    @Async("categoryAsyncExecutor")
    public CompletableFuture<List<ImageModelWithDefects>> processAllPhotosAsync(List<Photo> photos, Long startNum) {
        photos.sort(Comparator.comparingLong(Photo::getId)); // TODO нормальное реализовать

        log.debug("Start all photos processing {}", Thread.currentThread().getName());
        List<CompletableFuture<ImageModelWithDefects>> futures = Collections.synchronizedList(new ArrayList<>());
        for (Photo photo : photos) {
            futures.add(fetchAndAnalyzeAndSavePhoto(photo, startNum++).thenApply(imageMapper::mapToImageModelWithDefects));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenApply(f -> {
            log.debug("End all photos processing {}", Thread.currentThread().getName());
            return futures.stream().map(CompletableFuture::join).toList();
        });
    }

    public CompletableFuture<ImageModel> fetchPhoto(UUID uuid) {
        ProcessingImageDto processingImageDto = photoMapper.mapToProcessingImage(uuid);
        return imageProcessingFacade.fetchImage(processingImageDto).thenApply(imageMapper::mapToImageModel);
    }

    @Async("categoryAsyncExecutor") // TODO другой исполнитель, только на обработку фотографий которые не отправлены
    public void fetchAnalyzeAndSave(Photo photo) {
        if (!rabbitMQService.photoWasSent(photo.getId())) {
            fetchAndAnalyzeAndSavePhoto(photo, -1);
        } else {
            log.debug("Photo {} already in queue for analyze", photo.getId());
        }
    }

    private CompletableFuture<ProcessingImageDto> fetchAndAnalyzeAndSavePhoto(Photo photo, long photoNum) {
        ProcessingImageDto processingImageDto = photoMapper.mapToProcessingImage(photo, photoNum);
        return imageProcessingFacade.fetchAnalyzeAndReceiveImage(processingImageDto);
    }

    // GPT

    public CompletableFuture<GptReceiverDto> analyzeAllDefects(DocumentModel documentModel, long inspectionId) {
        GptSenderDto gptSenderDto = senderMapper.mapToGptSenderDto(documentModel);
        return rabbitMQService.sendAndReceiveGptResult(gptSenderDto, inspectionId);
    }
}
