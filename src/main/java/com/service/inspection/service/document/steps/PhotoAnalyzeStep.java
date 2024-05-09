package com.service.inspection.service.document.steps;

import com.service.inspection.dto.document.PhotoDefectsDto;
import com.service.inspection.mapper.PhotoMapper;
import com.service.inspection.service.document.ProcessingImageDto;
import com.service.inspection.service.rabbit.RabbitMQService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@AllArgsConstructor
public class PhotoAnalyzeStep extends AbstractImageProcessingStep {

    private final RabbitMQService rabbitMQService;
    private final PhotoMapper photoMapper;

    @Override
    public CompletableFuture<ProcessingImageDto> executeProcess(ProcessingImageDto imageModel) {

        CompletableFuture<PhotoDefectsDto> rabbitMessageFuture = rabbitMQService.findById(imageModel.getId());

        if (rabbitMessageFuture == null) {
            rabbitMessageFuture =
                    rabbitMQService.sendAndReceiveImageDefects(photoMapper.mapToCkSendProcessDto(imageModel), imageModel.getId());
            log.debug("Send photo {} to analyze", imageModel.getId());
        } else {
            log.debug("Don't need to analyze photo {}. Already in queue", imageModel.getId());
            imageModel.setNeedToSave(false);
        }

        return rabbitMessageFuture.thenApply(defectsDto -> {
            imageModel.setPhotoDefectsDto(defectsDto);
            return imageModel;
        });
    }

    @Override
    public boolean canCompleteStep(ProcessingImageDto currentState) {
        return currentState.getPhotoBytes() != null;
    }

    @Override
    boolean isNeedToRun(ProcessingImageDto imageDto) {
        return Optional.ofNullable(imageDto.getPhotoDefectsDto()).map(PhotoDefectsDto::getDefectsDto).isEmpty() &&
                super.isNeedToRun(imageDto);
    }
}
