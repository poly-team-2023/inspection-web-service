package com.service.inspection.service.document.steps;

import com.service.inspection.configs.BucketName;
import com.service.inspection.service.StorageService;
import com.service.inspection.service.document.ProcessingImageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Order(1)
@RequiredArgsConstructor
public class ImageStoringStep extends AbstractImageProcessingStep {
    private final StorageService storageService;

    @Override
    public CompletableFuture<ProcessingImageDto> executeProcess(CompletableFuture<ProcessingImageDto> processingImageDto) {
        ProcessingImageDto imageModel = processingImageDto.join();

        if (imageModel.getPhotoBytes() == null) {
            try {
                StorageService.BytesWithContentType file =
                        storageService.getFile(BucketName.DEFAULT_IMAGE_BUCKET, imageModel.getUuid().toString());
                imageModel.setPhotoBytes(file.getBytes());
            } catch (Exception e) {
                return CompletableFuture.failedFuture(e);
            }
        }

        if (imageModel.getId() == null) {
            return CompletableFuture.completedFuture(imageModel);
        }

        return nextStep.executeProcess(processingImageDto);
    }

    @Override
    public boolean isValidImageStep(ProcessingImageDto currentState) {
        return currentState.getId() == null;
    }
}
