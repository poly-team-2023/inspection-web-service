package com.service.inspection.service;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.google.common.base.Stopwatch;
import com.service.inspection.configs.BucketName;
import com.service.inspection.document.DocumentModel;
import com.service.inspection.document.model.CategoryModel;
import com.service.inspection.entities.Inspection;
import com.service.inspection.entities.User;
import com.service.inspection.entities.enums.ProgressingStatus;
import com.service.inspection.mapper.document.DocumentMapper;
import com.service.inspection.repositories.InspectionRepository;
import com.service.inspection.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class DocumentService {

    private final RabbitTemplate rabbitTemplate;
    private final Queue inspectionQueue;
    private final InspectionRepository inspectionRepository;
    private final UserRepository userRepository;
    private final DocumentMapper documentMapper;
    private final ResourceLoader resourceLoader;
    private final StorageService storageService;
    private final String templatePath;
    private final Configure config;


    public DocumentService(RabbitTemplate rabbitTemplate, @Qualifier(value = "inspectionTask") Queue inspectionQueue,
                           DocumentMapper documentMapper, Configure config,
                           InspectionRepository inspectionRepository, UserRepository userRepository,
                           ResourceLoader resourceLoader, StorageService storageService, String templatePath) {
        this.rabbitTemplate = rabbitTemplate;
        this.inspectionQueue = inspectionQueue;
        this.inspectionRepository = inspectionRepository;
        this.userRepository = userRepository;
        this.documentMapper = documentMapper;
        this.resourceLoader = resourceLoader;
        this.storageService = storageService;
        this.templatePath = templatePath;
        this.config = config;
    }

    @Transactional
    public void addInspectionInQueueToProcess(Inspection inspection, User user) {
        UserIdInspectionIdDto userIdInspectionIdDto = new UserIdInspectionIdDto(user.getId(), inspection.getId());
        // TODO стоит продумать логику повторения в случае если падает rabbitMQ
        rabbitTemplate.convertAndSend(inspectionQueue.getActualName(), userIdInspectionIdDto);

        inspection.setStatus(ProgressingStatus.WAIT_ANALYZE);
        inspectionRepository.save(inspection);
    }

    @RabbitListener(queues = "${rabbit.queue.main}", messageConverter = "")
    public void startProcessingInspection(UserIdInspectionIdDto dto) {
        Stopwatch timer = Stopwatch.createStarted();
        Inspection inspection = inspectionRepository.findById(dto.getInspectionId()).orElse(null);
        User user = userRepository.findById(dto.getUserId()).orElse(null);
        log.info("Start creating inspection document for inspection {}", inspection.getId());

        Hibernate. initialize(user.getEquipment());

        List<CompletableFuture<Void>> futureResult = Collections.synchronizedList(new ArrayList<>());
        DocumentModel documentModel = documentMapper.mapToDocumentModel(inspection, user, futureResult);
        CompletableFuture.allOf(futureResult.toArray(new CompletableFuture[0])).thenAccept(x -> {
            if (documentModel.getCategories() != null) {
                documentModel.getCategories().sort(Comparator.comparingLong(CategoryModel::getCategoryNum));
            }
            try (
                    XWPFTemplate template = XWPFTemplate
                            .compile(resourceLoader.getResource(templatePath).getInputStream(), config)
                            .render(documentModel);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
            ) {

                template.write(byteArrayOutputStream);
                InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                UUID fileUuid = saveDocxFileFile(inspection, inputStream);
                log.info("Saved file uuid {} for inspection {}. Takes: {}", fileUuid, inspection.getId(), timer.stop());
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }).join();
    }


    @Data
    @AllArgsConstructor
    public static class UserIdInspectionIdDto implements Serializable {

        @Serial
        private static final long serialVersionUID = 14710374104871234L;

        private Long userId;
        private Long inspectionId;
    }

    protected UUID saveDocxFileFile(Inspection inspection, InputStream inputStream) {
        Inspection fromDB = inspectionRepository.findById(inspection.getId()).orElse(null);
        if (fromDB == null) return null;

        UUID uuid = UUID.randomUUID();

        fromDB.setStatus(ProgressingStatus.READY);
        fromDB.setReportUuid(uuid);

        inspectionRepository.save(fromDB);
        storageService.saveFile(BucketName.DOCUMENT, uuid.toString(), inputStream);
        return uuid;
    }
}
