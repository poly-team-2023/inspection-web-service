package com.service.inspection.service.document.steps;

import com.service.inspection.service.document.ProcessingImageDto;

import java.util.concurrent.CompletableFuture;

public interface ImageProcessingStep {
    void setNextStep(ImageProcessingStep step);

    public CompletableFuture<ProcessingImageDto> executeProcess(CompletableFuture<ProcessingImageDto> processingImageDto);

}
