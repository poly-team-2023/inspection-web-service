package com.service.inspection.service.document.steps;

import com.service.inspection.configs.BucketName;
import com.service.inspection.service.StorageService;
import com.service.inspection.service.document.ProcessingImageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(1)
@RequiredArgsConstructor
public class ImageStoringStep extends AbstractImageProcessingStep {
    private final StorageService storageService;

    @Override
    public void executeProcess(ProcessingImageDto processingImageDto) {

        try {
            StorageService.BytesWithContentType file =
                    storageService.getFile(BucketName.DEFAULT_IMAGE_BUCKET, processingImageDto.getUuid().toString());
            processingImageDto.setPhotoBytes(file.getBytes());
        } catch (Exception e) {
            return;
        }
//        processingImageDto.setPhotoBytes(file.getBytes());

        nextStep.executeProcess(processingImageDto);
    }
}
