package com.service.inspection.service.document.steps;

import com.service.inspection.entities.Photo;
import com.service.inspection.mapper.PhotoMapper;
import com.service.inspection.repositories.PhotoRepository;
import com.service.inspection.service.document.ProcessingImageDto;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Service
@Order(3)
public class ImageStoringStep extends AbstractImageProcessingStep {

    private final PhotoRepository photoRepository;
    private final PhotoMapper photoMapper;

    @Override
    public boolean isValidImageStep(ProcessingImageDto nextStep) {
        return true;
    }


    @Override
    public CompletableFuture<ProcessingImageDto> executeProcess(CompletableFuture<ProcessingImageDto> processingImageDto) {
        ProcessingImageDto imageModel = processingImageDto.join();

        if (imageModel.getPhotoDefectsDto() != null) {
            photoRepository.findById(imageModel.getId()).ifPresent(photo -> {

                photo.setDefectsCoords(photoMapper.mapToPhotos(imageModel.getPhotoDefectsDto().getDefectsDto()));
                photoRepository.save(photo);

            });
        }

        return CompletableFuture.completedFuture(imageModel);
    }
}
