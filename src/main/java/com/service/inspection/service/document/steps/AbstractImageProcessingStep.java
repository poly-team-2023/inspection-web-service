package com.service.inspection.service.document.steps;

import com.service.inspection.service.document.ProcessingImageDto;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
@Slf4j
public abstract class AbstractImageProcessingStep implements ImageProcessingStep {

    public CompletableFuture<ProcessingImageDto> executeProcess(CompletableFuture<ProcessingImageDto> processingImageDto) {

        ProcessingImageDto imageDto = processingImageDto.handle((o, e) -> {
            if (e != null) {
                log.error("Error while execute last stage, because {}", e.getMessage());
                return null;
            }
            return o;
        }).join(); // TODO подумать о таймаутах стадии, как об абстрактном методе с временем

        if (imageDto == null) return CompletableFuture.failedFuture(new Throwable("Previous future execute with error"));

        if (!isNeedToRun(imageDto)) return CompletableFuture.completedFuture(imageDto);

        if (!canCompleteStep(imageDto)) {
            log.error("Can't complete stage");
            return CompletableFuture.failedFuture(new Throwable("Can't complete stage"));
        }

        return executeProcess(imageDto);
    }

    protected abstract CompletableFuture<ProcessingImageDto> executeProcess(ProcessingImageDto processingImageDto);

    protected abstract boolean canCompleteStep(ProcessingImageDto processingImageDto);

    boolean isNeedToRun(ProcessingImageDto processingImageDto) {
        return true;
    }
}
