package com.service.inspection.service.document.steps;

public abstract class AbstractImageProcessingStep implements ImageProcessingStep {

    protected ImageProcessingStep nextStep;

    @Override
    public void setNextStep(ImageProcessingStep nextStep) {
        this.nextStep = nextStep;
    }

//    public abstract void executeProcess(TestImageModel imageModel);
}
