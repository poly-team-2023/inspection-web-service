package com.service.inspection.service;

import com.service.inspection.entities.Photo;
import com.service.inspection.repositories.PhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataService {

    private final PhotoRepository photoRepository;
    private final Map<Long, BlockingQueue<Set<Photo.Defect>>> photosUpdatingStorage;

    // TODO реализовать удаление из мапы если происходит удаление фотографии

    @Transactional
    public void updatePhotoInfo(Long photoId, Set<Photo.Defect> defects) {

        Photo photo = photoRepository.findById(photoId).orElse(null);
        if (photo == null) return;

        if (photosUpdatingStorage.containsKey(photoId)) {
            if (!photosUpdatingStorage.get(photoId).offer(defects)) {
                log.error("Cant insert photo {} info about defects", photoId);
            } else {
                photo.setDefectsCoords(defects);
                photoRepository.save(photo);
                log.debug("Successfully add defects photo {} info", photoId);
            }
        } else {
            log.warn("I dont need info about photo {}", photoId);
        }
    }
}
