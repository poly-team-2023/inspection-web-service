package com.service.inspection.service.document;

import com.service.inspection.service.document.steps.FetchBytesStep;
import com.service.inspection.service.document.steps.DefectsSavingStep;
import com.service.inspection.service.document.steps.PhotoAnalyzeStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ImageProcessingFacade {

    private final ProcessingPhotoChain onlyFetchChain;
    private final ProcessingPhotoChain fetchAndAnalyzeAndSaveChain;

    @Autowired
    public ImageProcessingFacade(DefectsSavingStep defectsSavingStep, FetchBytesStep fetchBytesStep,
                                 PhotoAnalyzeStep photoAnalyzeStep) {

        fetchAndAnalyzeAndSaveChain = new ProcessingPhotoChain(fetchBytesStep, photoAnalyzeStep, defectsSavingStep);
        onlyFetchChain = new ProcessingPhotoChain(fetchBytesStep);
    }

    @Async("photosAsyncExecutor")
    public CompletableFuture<ProcessingImageDto> fetchAnalyzeAndReceiveImage(ProcessingImageDto photo) {
        log.debug("Start execute full chain photo {}, in {}", photo.getId(), Thread.currentThread().getName());
        return fetchAndAnalyzeAndSaveChain.apply(photo);
    }

    @Async("photosAsyncExecutor")
    public CompletableFuture<ProcessingImageDto> fetchImage(ProcessingImageDto photo) {
        log.info("Start fetch photo {}, in {}", photo.getId(), Thread.currentThread().getName());
        return onlyFetchChain.apply(photo);
    }
}
