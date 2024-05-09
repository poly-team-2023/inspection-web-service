package com.service.inspection.service.document;

import com.google.common.base.Preconditions;
import com.service.inspection.service.document.steps.AbstractImageProcessingStep;
import com.service.inspection.service.document.steps.ImageProcessingStep;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Slf4j
public class ProcessingPhotoChain implements Function<ProcessingImageDto, CompletableFuture<ProcessingImageDto>> {

    private final AbstractImageProcessingStep[] chain;

    public ProcessingPhotoChain(AbstractImageProcessingStep... steps) {
        Preconditions.checkState(steps != null && steps.length != 0);
        chain = steps;
    }

    @Override
    public CompletableFuture<ProcessingImageDto> apply(ProcessingImageDto processingImageDto) {
        CompletableFuture<ProcessingImageDto> imageProcessing = CompletableFuture.completedFuture(processingImageDto);

        for (ImageProcessingStep step : chain) {
            imageProcessing = step.executeProcess(imageProcessing);
        }
        return imageProcessing; // TODO на всю цепочку исполнения можно повесить таймаут
    }
}
