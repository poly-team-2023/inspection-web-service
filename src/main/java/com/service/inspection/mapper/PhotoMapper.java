package com.service.inspection.mapper;

import com.service.inspection.dto.document.CkImageProcessingDto;
import com.service.inspection.dto.document.PhotoDefectsDto;
import com.service.inspection.dto.inspection.CategoryWithFile;
import com.service.inspection.entities.Category;
import com.service.inspection.entities.Photo;
import com.service.inspection.entities.Plan;
import com.service.inspection.service.document.ProcessingImageDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Base64;
import java.util.Set;
import java.util.UUID;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"
)
public interface PhotoMapper {
    CategoryWithFile.PhotoDto mapToPhotoDto(Photo photo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recommendation", ignore = true)
    @Mapping(source = "plan", target = "plan")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "uuid", target = "fileUuid")
    Photo mapToPhoto(String name, UUID uuid, Category category, Plan plan);

    public Set<Photo.Defect> mapToPhotos(Set<PhotoDefectsDto.DefectDto> dto);

    @Mapping(source = "defectName", target = "name")
    Photo.Defect mapToPhotos(PhotoDefectsDto.DefectDto dto);

    @Mapping(source = "photo.fileUuid", target = "uuid")
    @Mapping(source = "photo.id", target = "id")
    @Mapping(source = "photo.defectsCoords", target = "defects")
    @Mapping(source = "photoNum", target = "photoNum")
    ProcessingImageDto mapToProcessingImage(Photo photo, Long photoNum);

    ProcessingImageDto mapToProcessingImage(UUID uuid);

    @Mapping(target = "imgBASE64", source = "photoBytes")
    @Mapping(target = "nnMode", constant = "facade")
    public abstract CkImageProcessingDto mapToCkSendProcessDto(ProcessingImageDto processingImageDto);

    default String toBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
