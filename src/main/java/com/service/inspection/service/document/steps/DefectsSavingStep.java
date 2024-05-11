package com.service.inspection.service.document.steps;

import com.service.inspection.mapper.PhotoMapper;
import com.service.inspection.repositories.PhotoRepository;
import com.service.inspection.service.document.ProcessingImageDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Service
public class DefectsSavingStep extends AbstractImageProcessingStep {

    private final PhotoRepository photoRepository;
    private final PhotoMapper photoMapper;


    @Override
    public CompletableFuture<ProcessingImageDto> executeProcess(ProcessingImageDto imageModel) {
        photoRepository.findById(imageModel.getId()).ifPresent(photo -> {
            photo.setDefectsCoords(photoMapper.mapToPhotos(imageModel.getPhotoDefectsDto().getDefectsDto()));
            photo.setPhotoNum(imageModel.getPhotoNum());
            photoRepository.save(photo);
        });
        return CompletableFuture.completedFuture(imageModel);
    }

    @Override
    public boolean canCompleteStep(ProcessingImageDto imageDto) {
        return true;
    }

    @Override
    boolean isNeedToRun(ProcessingImageDto imageDto) {
        return imageDto.isNeedToSave() && imageDto.getPhotoDefectsDto() != null && super.isNeedToRun(imageDto);
    }
}
