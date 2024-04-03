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

    @RabbitListener(queues = "doc.task.dev", messageConverter = "")
    @Transactional
    public void startProcessingInspection(UserIdInspectionIdDto dto) {
        Stopwatch timer = Stopwatch.createStarted();
        Inspection inspection = inspectionRepository.findById(dto.getInspectionId()).orElse(null);
        User user = userRepository.findById(dto.getUserId()).orElse(null);

        if (inspection == null || user == null) {
            log.error("CANT GET INSPECTION ID {} FOR USER ID {}. FIND ONLY USER {} AND INSPECTION {} !!!",
                    dto.getInspectionId(), dto.getUserId(), user, inspection);
            return;
        }

        log.info("Start creating inspection document for inspection {}. Have memory {}: ", inspection.getId(),
                Runtime.getRuntime().freeMemory());
        List<CompletableFuture<Void>> futureResult = Collections.synchronizedList(new ArrayList<>());
        DocumentModel documentModel = documentMapper.mapToDocumentModel(inspection, user, futureResult);
        CompletableFuture.allOf(futureResult.toArray(new CompletableFuture[0])).thenRun(() -> {
            if (documentModel.getCategories() != null) {
                documentModel.getCategories().sort(Comparator.comparingLong(CategoryModel::getCategoryNum));
            }
            try (
                    XWPFTemplate template = XWPFTemplate
                            .compile(resourceLoader.getResource(templatePath).getInputStream(), config)
                            .render(documentModel);
            ) {

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

                template.write(byteArrayOutputStream);
                UUID fileUuid = saveDocxFileFile(inspection, inputStream);
                log.info("Saved file uuid {} for inspection {}. Takes: {}. Have memory: {}", fileUuid,
                        inspection.getId(), timer.stop(), Runtime.getRuntime().freeMemory());

            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }).handle((res, exp) -> {
            if (exp != null) {
                log.error("WTF SMT REALLY BROKE WHILE GENERATE {} FOR {}. THROW: {}", inspection, user, exp.getMessage());
            }
            return null;
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
        UUID uuid = UUID.randomUUID();

        inspection.setStatus(ProgressingStatus.READY);
        inspection.setReportUuid(uuid);

        inspectionRepository.save(inspection);
        storageService.saveFile(BucketName.DOCUMENT, uuid.toString(), inputStream);
        return uuid;
    }

}
