package com.service.inspection.service.document.steps;

import com.service.inspection.service.document.ProcessingImageDto;

public interface ImageProcessingStep {
    void setNextStep(ImageProcessingStep step);
    void executeProcess(ProcessingImageDto processingImageDto);

//    boolean skipStep(Collection<ImageProcessingStep> excludeSteps);
}
