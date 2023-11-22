package com.service.inspection.service.document.steps;

import com.service.inspection.configs.BucketName;
import com.service.inspection.service.StorageService;
import com.service.inspection.service.document.TestImageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(1)
@RequiredArgsConstructor
public class ImageStoringStep extends AbstractImageProcessingStep {
    private final StorageService storageService;

    @Override
    public void executeProcess(TestImageModel testImageModel) {

        StorageService.BytesWithContentType file =
                storageService.getFile(BucketName.DEFAULT_IMAGE_BUCKET, testImageModel.getPhoto().getFileUuid().toString());

        testImageModel.setPhotoBytes(file.getBytes());

        nextStep.executeProcess(testImageModel);
    }
}
