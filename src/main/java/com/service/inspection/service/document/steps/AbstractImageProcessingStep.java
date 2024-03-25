package com.service.inspection.service.document.steps;

import com.service.inspection.service.document.ProcessingImageDto;

public abstract class AbstractImageProcessingStep implements ImageProcessingStep {

    protected ImageProcessingStep nextStep;

    @Override
    public void setNextStep(ImageProcessingStep nextStep) {
        this.nextStep = nextStep;
    }

    public abstract boolean isValidImageStep(ProcessingImageDto nextStep);
}
