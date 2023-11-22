package com.service.inspection.service.document.steps;

import com.service.inspection.repositories.InspectionRepository;
import com.service.inspection.repositories.PhotoRepository;
import com.service.inspection.service.document.TestImageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Slf4j
@Order(2)
@Service
@RequiredArgsConstructor
public class ListenerPhotoStage extends AbstractImageProcessingStep {

    private static final Integer COUNT_TRIES = 5;
    private final PhotoRepository photoRepository;
    private final InspectionRepository inspectionRepository;

    @Override
    public void executeProcess(TestImageModel imageModel) {

        log.info("Start listen db changing" + Thread.currentThread().getName());

        int tries = 0;

        while (tries != COUNT_TRIES) {
            if (photoRepository.findById(imageModel.getPhoto().getId()).orElse(null)
                    .getRecommendation() != null) {
                log.info("Find db changing" + Thread.currentThread().getName() + " " + imageModel.getPhoto().getId());
                return;
            }

            tries += 1;

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
        log.info("End listen db changing" + Thread.currentThread().getName() + ". Not found");
    }
}
