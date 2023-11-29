package com.service.inspection.service;

import com.service.inspection.entities.Photo;
import com.service.inspection.repositories.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataService {

    private final PhotoRepository photoRepository;
    private final Map<Long, Optional<Set<Photo.Defect>>> photosUpdatingStorage;

    // TODO реализовать удаление из мапы если происходит удаление фотографии

    @Transactional
    public void updatePhotoInfo(Long photoId, Set<Photo.Defect> defects) {
        Photo photo = photoRepository.findById(photoId).orElseThrow(RuntimeException::new);

        if (photosUpdatingStorage.containsKey(photoId) && photo != null) {
            photosUpdatingStorage.put(photoId, Optional.of(defects));
            photo.setDefectsCoords(defects);
            photoRepository.save(photo);
        }
    }
}
