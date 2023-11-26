package com.service.inspection.service.document.steps;

import com.service.inspection.entities.Photo;
import com.service.inspection.repositories.PhotoRepository;
import com.service.inspection.service.document.ProcessingImageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Order(2)
@Service
@RequiredArgsConstructor
public class ListenerPhotoStage extends AbstractImageProcessingStep {

    private static final Integer COUNT_TRIES = 5;
    private final PhotoRepository photoRepository;
    private final Map<Long, Optional<Set<Photo.Defect>>> photosUpdatingStorage;

    @Override
    public void executeProcess(ProcessingImageDto imageModel) {

        // TODO send request to NN

        int tries = 0;
        Set<Photo.Defect> defects = photoRepository.findById(imageModel.getId())
                .orElse(null)
                .getDefectsCoords();

        if (defects == null) {
            photosUpdatingStorage.putIfAbsent(imageModel.getId(), Optional.empty());
            log.info("Start listen photos changing" + Thread.currentThread().getName());
            while (tries != COUNT_TRIES) {
                Optional<Set<Photo.Defect>> defects1 = photosUpdatingStorage.get(imageModel.getId());

                if (defects1.isPresent()) {
                    imageModel.setDefects(defects1.get());
                    photosUpdatingStorage.remove(imageModel.getId());
                    log.info("End listen photos changing" + Thread.currentThread().getName() + ".Found !! URAA");
                }
                tries += 1;


                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    log.error(e.toString());
                }
            }

            log.info("End listen photos changing" + Thread.currentThread().getName() + ". Not found");
        }
    }
}
