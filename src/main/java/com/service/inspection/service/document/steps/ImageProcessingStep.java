package com.service.inspection.service.document.steps;

import com.service.inspection.service.document.TestImageModel;

import java.util.Collection;

public interface ImageProcessingStep {
    void setNextStep(ImageProcessingStep step);
    void executeProcess(TestImageModel testImageModel);

//    boolean skipStep(Collection<ImageProcessingStep> excludeSteps);
}
