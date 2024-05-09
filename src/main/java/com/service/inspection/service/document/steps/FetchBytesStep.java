package com.service.inspection.service.document.steps;

import com.service.inspection.configs.BucketName;
import com.service.inspection.service.StorageService;
import com.service.inspection.service.document.ProcessingImageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class FetchBytesStep extends AbstractImageProcessingStep {
    private final StorageService storageService;

    @Override
    public CompletableFuture<ProcessingImageDto> executeProcess(ProcessingImageDto imageModel) {
        try {
            StorageService.BytesWithContentType file =
                    storageService.getFile(BucketName.DEFAULT_IMAGE_BUCKET, imageModel.getUuid().toString()); // TODO делать асинхронно
            imageModel.setPhotoBytes(file.getBytes());
            return CompletableFuture.completedFuture(imageModel);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public boolean canCompleteStep(ProcessingImageDto imageDto) {
        return imageDto.getUuid() != null;
    }

    @Override
    boolean isNeedToRun(ProcessingImageDto imageDto) {
        return imageDto.getPhotoBytes() == null && super.isNeedToRun(imageDto);
    }
}
