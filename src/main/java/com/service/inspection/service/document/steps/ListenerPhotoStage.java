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
    private final PhotoRepository photoRepository;


    public ListenerPhotoStage(AsyncRabbitTemplate asyncRabbitTemplate, ObjectMapper mapper,
                              @Qualifier("imageTask") Queue queue, PhotoMapper photoMapper, PhotoRepository photoRepository) {
        this.asyncRabbitTemplate = asyncRabbitTemplate;
        this.mapper = mapper;
        this.queue = queue;
        this.photoMapper = photoMapper;
        this.photoRepository = photoRepository;
    }

    @Override
    public CompletableFuture<ProcessingImageDto> executeProcess(CompletableFuture<ProcessingImageDto> imageModelFuture) {
        ProcessingImageDto imageModel = imageModelFuture.join();

        if (isValidImageStep(imageModel)) {
            return CompletableFuture.failedFuture(new Throwable());
        }

        Set<Photo.Defect> defects = imageModel.getDefects();
        if (defects == null) {
            try {
                Message message = MessageBuilder
                        .withBody(mapper.writeValueAsBytes(photoMapper.mapToCkSendProcessDto(imageModel)))
                        .setContentType(MessageProperties.CONTENT_TYPE_JSON).build();

                log.debug("Send image {} to process", imageModel.getId());
                return asyncRabbitTemplate.sendAndReceive(queue.getActualName(), message)
                        .thenApply(mes -> {
                            log.debug("Get image {} after process", imageModel.getId());
                            try {
                                PhotoDefectsDto i = mapper.readValue(mes.getBody(), PhotoDefectsDto.class);
                                imageModel.setDefects(photoMapper.mapToPhotos(i.getDefectsDto()));

                                // TODO реализовать это нормально
                                Photo photo = photoRepository.findById(imageModel.getId()).orElse(null);
                                photo.setDefectsCoords(photoMapper.mapToPhotos(i.getDefectsDto()));
                                photoRepository.save(photo);

                                return imageModel;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }).handle((el, err) -> err == null ? el : null);

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
