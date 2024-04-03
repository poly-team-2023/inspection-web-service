package com.service.inspection.mapper;

import com.google.common.base.Preconditions;
import com.service.inspection.dto.document.CkImageProcessingDto;
import com.service.inspection.dto.document.PhotoDefectsDto;
import com.service.inspection.dto.inspection.CategoryWithFile;
import com.service.inspection.entities.Category;
import com.service.inspection.entities.Photo;
import com.service.inspection.entities.Plan;
import com.service.inspection.service.document.ProcessingImageDto;
import org.mapstruct.*;

import java.util.*;
import java.util.stream.IntStream;

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

    Set<Photo.Defect> mapToPhotos(Set<PhotoDefectsDto.DefectDto> dto);

    @Mapping(source = "defectName", target = "name")
    @Mapping(source = ".", target = "coords")
    Photo.Defect mapToPhotos(PhotoDefectsDto.DefectDto dto);

    default List<Photo.Coord> toCoords(PhotoDefectsDto.DefectDto dto) {
        if (dto == null || dto.getCoordsX() == null || dto.getCoordsY() == null) return null;
        Preconditions.checkState(dto.getCoordsY().size() == dto.getCoordsX().size(),
                "Cant have different count of X and Y");

        List<Photo.Coord> list = new ArrayList<>();
        List<Integer> coordsX = dto.getCoordsX();
        List<Integer> coordsY = dto.getCoordsY();

        IntStream.range(0, coordsX.size()).forEach(i -> list.add(new Photo.Coord(coordsX.get(i), coordsY.get(i))));
        return list;
    }

    @Mapping(source = "photo.fileUuid", target = "uuid")
    @Mapping(source = "photo.id", target = "id")
    @Mapping(source = "photo.defectsCoords", target = "photoDefectsDto")
    @Mapping(source = "photoNum", target = "photoNum")
    ProcessingImageDto mapToProcessingImage(Photo photo, Long photoNum);

    ProcessingImageDto mapToProcessingImage(UUID uuid);

    @Mapping(target = "imgBASE64", source = "photoBytes")
    @Mapping(target = "nnMode", constant = "facade")
    public abstract CkImageProcessingDto mapToCkSendProcessDto(ProcessingImageDto processingImageDto);

    default String toBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    Set<PhotoDefectsDto.DefectDto> toDefectsDto(Set<Photo.Defect> defectDtos);

    @Mapping(source = "name", target = "defectName")
    PhotoDefectsDto.DefectDto defectToDefectDto(Photo.Defect defect);

    @AfterMapping
    default void coordsToDefectDto(@MappingTarget PhotoDefectsDto.DefectDto target, Photo.Defect source) {

        List<Integer> coordsX = new ArrayList<>();
        List<Integer> coordsY = new ArrayList<>();

        Optional.ofNullable(source).map(Photo.Defect::getCoords).map(Collection::stream)
                .ifPresent(coordStream -> coordStream.forEachOrdered(coord -> {
                    coordsX.add(coord.getX());
                    coordsY.add(coord.getY());
                }));

        target.setCoordsY(coordsY);
        target.setCoordsX(coordsX);
    }

    default PhotoDefectsDto toPhotoDefectsDto(Set<Photo.Defect> defects) {
        if (defects == null) return null;

        PhotoDefectsDto photoDefectsDto = new PhotoDefectsDto();
        Set<PhotoDefectsDto.DefectDto> defectsDtos = toDefectsDto(defects);
        photoDefectsDto.setDefectsDto(defectsDtos);
        return photoDefectsDto;
    }
}
