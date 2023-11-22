package com.service.inspection.service.document;

import com.service.inspection.service.document.steps.ImageProcessingStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ImageProcessingFacade {

    private final ImageProcessingStep firstStep;

    @Autowired
    public ImageProcessingFacade(List<ImageProcessingStep> steps) {
        firstStep = steps.get(0);
        for (int i = 0; i < steps.size(); i++) {
            var current = steps.get(i);
            var next = i < steps.size() - 1 ? steps.get(i + 1) : null;
            current.setNextStep(next);
        }
    }

    @Async("fileAsyncExecutor")
    public CompletableFuture<TestImageModel> processPhoto(TestImageModel photo) {
        log.info("Start " + Thread.currentThread().getName());
        firstStep.executeProcess(photo);
        log.info("End " + Thread.currentThread().getName());
        return CompletableFuture.completedFuture(photo);
    }
}
