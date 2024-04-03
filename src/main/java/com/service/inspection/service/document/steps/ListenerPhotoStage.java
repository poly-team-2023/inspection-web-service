package com.service.inspection.service.document.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.inspection.dto.document.PhotoDefectsDto;
import com.service.inspection.entities.Photo;
import com.service.inspection.mapper.PhotoMapper;
import com.service.inspection.repositories.PhotoRepository;
import com.service.inspection.service.document.ProcessingImageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Order(2)
@Service
public class ListenerPhotoStage extends AbstractImageProcessingStep {

    private final AsyncRabbitTemplate asyncRabbitTemplate;
    private final ObjectMapper mapper;
    private final Queue queue;
    private final PhotoMapper photoMapper;


    public ListenerPhotoStage(AsyncRabbitTemplate asyncRabbitTemplate, ObjectMapper mapper,
                              @Qualifier("imageTask") Queue queue, PhotoMapper photoMapper) {
        this.asyncRabbitTemplate = asyncRabbitTemplate;
        this.mapper = mapper;
        this.queue = queue;
        this.photoMapper = photoMapper;
    }

    @Override
    public CompletableFuture<ProcessingImageDto> executeProcess(CompletableFuture<ProcessingImageDto> imageModelFuture) {
        ProcessingImageDto imageModel = imageModelFuture.join();
        PhotoDefectsDto defects = imageModel.getPhotoDefectsDto();

        if (defects == null || defects.getDefectsDto() == null) {
            try {
                Message message = MessageBuilder
                        .withBody(mapper.writeValueAsBytes(photoMapper.mapToCkSendProcessDto(imageModel)))
                        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                        .setCorrelationId(UUID.randomUUID().toString()).build();

                log.debug("Send image {} to process", imageModel.getId());

                return nextStep.executeProcess(asyncRabbitTemplate.sendAndReceive(queue.getActualName(), message)
                        .thenApply(mes -> {
                            log.debug("Get image {} after process", imageModel.getId());
                            try {
                                PhotoDefectsDto i = mapper.readValue(mes.getBody(), PhotoDefectsDto.class);
                                imageModel.setPhotoDefectsDto(i);
                                log.debug(String.valueOf(i));
                                return imageModel;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .orTimeout(1, TimeUnit.HOURS)
                        .handle((el, err) -> err == null ? el : null));
            } catch (Exception e) {
                return CompletableFuture.failedFuture(new Throwable());
            }
        }
        return CompletableFuture.completedFuture(imageModel);
    }

    @Override
    public boolean isValidImageStep(ProcessingImageDto currentState) {
        return currentState.getId() == null || currentState.getPhotoBytes() == null;
    }
}
