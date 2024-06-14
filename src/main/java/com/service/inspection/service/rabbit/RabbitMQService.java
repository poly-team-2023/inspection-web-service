package com.service.inspection.service.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.service.inspection.dto.document.CkImageProcessingDto;
import com.service.inspection.dto.document.GptReceiverDto;
import com.service.inspection.dto.document.GptSenderDto;
import com.service.inspection.dto.document.PhotoDefectsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class RabbitMQService {

    private final AsyncRabbitTemplate asyncRabbitTemplate;
    private final ObjectMapper mapper;
    private final Queue imagePublishQueue;
    private final Queue nlmQueue;

    private ConcurrentHashMap<Long, CompletableFuture<PhotoDefectsDto>> photoPending = new ConcurrentHashMap<>();

    public RabbitMQService(
            AsyncRabbitTemplate asyncRabbitTemplate, ObjectMapper mapper,
            @Qualifier("imageTask") Queue imagePublishQueue, @Qualifier("nlmTask") Queue nlmQueue) {
        this.asyncRabbitTemplate = asyncRabbitTemplate;
        this.mapper = mapper;
        this.imagePublishQueue = imagePublishQueue;
        this.nlmQueue = nlmQueue;
    }

    /**
     * Посылает запрос на обработку в очередь directReply и получает будущее с ответом.
     * В случае ошибки получаем пустой обьект, а timeout выставляется прямо в {@link AsyncRabbitTemplate}
     *
     * @param imageDto входные данные
     * @return дефекты
     */
    public CompletableFuture<PhotoDefectsDto> sendAndReceiveImageDefects(CkImageProcessingDto imageDto, long photoId) {
        Preconditions.checkState(imageDto != null && photoId > 0);

        Message message = createMessage(imageDto);
        if (message == null) {
            return CompletableFuture.failedFuture(new Throwable("Can't create message"));
        }

        CompletableFuture<PhotoDefectsDto> rabbitMessageFuture =
                asyncRabbitTemplate.sendAndReceive(imagePublishQueue.getActualName(), message).thenApply(m -> {
                    try {
                        return mapper.readValue(m.getBody(), PhotoDefectsDto.class);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        return null;
                    }
                });

        photoPending.put(photoId, rabbitMessageFuture);

        rabbitMessageFuture.handle((m, e) -> {
            photoPending.remove(photoId);
            return null;
        });


        return rabbitMessageFuture;
    }

    public boolean photoWasSent(long photoId) {
        return photoPending.contains(photoId);
    }

    public CompletableFuture<PhotoDefectsDto> findById(long photoId) {
        return photoPending.get(photoId);
    }

    public CompletableFuture<GptReceiverDto> sendAndReceiveGptResult(GptSenderDto senderDto, long inspectionId) {
        Preconditions.checkState(senderDto != null && inspectionId > 0);

        Message message = createMessage(senderDto);
        if (message == null) {
            return CompletableFuture.failedFuture(new Throwable("Can't create message"));
        }

        CompletableFuture<GptReceiverDto> documentAnalyzeFuture =
                asyncRabbitTemplate.sendAndReceive(nlmQueue.getActualName(), message).thenApply(o -> {
            try {
                return mapper.readValue(o.getBody(), GptReceiverDto.class);
            } catch (IOException e) {
                log.error(e.getMessage());
                return null;
            }
        });

       return documentAnalyzeFuture;
    }

    private <T> Message createMessage(T imageDto) {
        try {
            return MessageBuilder.withBody(mapper.writeValueAsBytes(imageDto))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
