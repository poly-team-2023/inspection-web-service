package com.service.inspection.mapper;

import com.service.inspection.dto.document.CkImageProcessingDto;
import com.service.inspection.dto.document.PhotoDefectsDto;
import com.service.inspection.dto.inspection.CategoryWithFile;
import com.service.inspection.dto.inspection.PhotoCreateDto;
import com.service.inspection.entities.Category;
import com.service.inspection.entities.Photo;
import com.service.inspection.entities.PhotoPlan;
import com.service.inspection.entities.Plan;
import com.service.inspection.repositories.CategoryRepository;
import com.service.inspection.repositories.CompanyRepository;
import com.service.inspection.repositories.EmployerRepository;
import com.service.inspection.repositories.PlanRepository;
import com.service.inspection.service.document.ProcessingImageDto;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;
import static software.amazon.awssdk.utils.BinaryUtils.toBase64;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PhotoMapperImpl.class, CommonMapperImpl.class})
class PhotoMapperTest {

    @Autowired
    private PhotoMapper photoMapper;

    @MockBean
    private CompanyRepository companyRepository;
    @MockBean
    private EmployerRepository employerRepository;
    @MockBean
    private PlanRepository planRepository;
    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private EntityFactory entityFactory;

    @Test
    void testPhotoMapper() {
        Photo.Coord coord = new Photo.Coord();
        coord.setX(1);
        coord.setX(2);

        Photo.Coord coord1 = new Photo.Coord();
        coord1.setX(3);
        coord1.setX(4);

        Photo.Coord coord2 = new Photo.Coord();
        coord2.setX(5);
        coord2.setX(6);

        Photo.Coord coord3 = new Photo.Coord();
        coord3.setX(7);
        coord3.setX(8);

        Photo.Defect photoDefect = new Photo.Defect();
        photoDefect.setName("defect1");
        photoDefect.setCoords(List.of(coord1));

        Photo.Defect photoDefect1 = new Photo.Defect();
        photoDefect1.setName("defect2");
        photoDefect1.setCoords(List.of(coord, coord2, coord3));

        Set<Photo.Defect> defects = new LinkedHashSet<>();
        defects.add(photoDefect);
        defects.add(photoDefect1);

        Photo photo = new Photo();
        photo.setId(1L);
        photo.setName("qwe123");
        photo.setDefectsCoords(defects);

        CategoryWithFile.PhotoDto photoDto = photoMapper.mapToPhotoDto(photo);

        assertThat(photoDto.getName()).isEqualTo(photo.getName());
        assertThat(photoDto.getId()).isEqualTo(photo.getId());

        assertThat(photoDto.getDefects()).element(0).satisfies(defectDto -> {
            assertThat(defectDto.getName()).isEqualTo(photoDefect.getName());
            assertThat(defectDto.getCoords()).extracting("x", "y")
                    .containsExactlyInAnyOrder(Tuple.tuple(coord1.getX(), coord1.getY()));
        });

        assertThat(photoDto.getDefects()).element(1).satisfies(defectDto -> {
            assertThat(defectDto.getName()).isEqualTo(photoDefect1.getName());
            assertThat(defectDto.getCoords()).extracting("x", "y").containsExactlyInAnyOrder(
                    Tuple.tuple(coord.getX(), coord.getY()), Tuple.tuple(coord2.getX(), coord2.getY()),
                    Tuple.tuple(coord3.getX(), coord3.getY()));
        });
    }

    @Test
    void shouldMapPhotoPlanToPhotoCreateDto() {
        PhotoPlan photoPlan = new PhotoPlan();
        photoPlan.setId(1L);
        photoPlan.setName("TestPhoto");
        photoPlan.setX(10.0);
        photoPlan.setY(20.0);

        PhotoCreateDto result = photoMapper.mapToPhotoCreateDto(photoPlan);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(photoPlan.getId());
        assertThat(result.getName()).isEqualTo(photoPlan.getName());
        assertThat(result.getX()).isEqualTo(photoPlan.getX());
        assertThat(result.getY()).isEqualTo(photoPlan.getY());
    }

    @Test
    void shouldReturnNullWhenPhotoPlanIsNull() {
        PhotoCreateDto result = photoMapper.mapToPhotoCreateDto(null);
        assertThat(result).isNull();
    }

    @Test
    void shouldMapNameUuidCategoryToPhoto() {
        String name = "TestPhoto";
        UUID uuid = UUID.randomUUID();
        Category category = new Category();
        category.setId(1L);

        Photo result = photoMapper.mapToPhoto(name, uuid, category);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getFileUuid()).isEqualTo(uuid);
        assertThat(result.getCategory()).isEqualTo(category);
    }

    @Test
    void shouldReturnNullWhenAllInputsAreNull() {
        Photo result = photoMapper.mapToPhoto(null, null, null);
        assertThat(result).isNull();
    }

    @Test
    void shouldMapPhotoCreateDtoUuidPlanIdToPhotoPlan() {
        PhotoPlan photo = new PhotoPlan();
        PhotoCreateDto photoCreateDto = new PhotoCreateDto();
        photoCreateDto.setName("TestPhoto");
        photoCreateDto.setX(10.0);
        photoCreateDto.setY(20.0);
        UUID uuid = UUID.randomUUID();
        Long planId = 1L;

        Plan expectedPlan = new Plan();
        expectedPlan.setId(4L);
        when(entityFactory.createPlanReferenceFromId(planId)).thenReturn(expectedPlan);

        photoMapper.mapToPhoto(photo, photoCreateDto, uuid, planId);

        assertThat(photo.getName()).isEqualTo(photoCreateDto.getName());
        assertThat(photo.getX()).isEqualTo(photoCreateDto.getX());
        assertThat(photo.getY()).isEqualTo(photoCreateDto.getY());
        assertThat(photo.getFileUuid()).isEqualTo(uuid);
        assertThat(photo.getPlan()).isEqualTo(expectedPlan);
        assertThat(photo.getLastUpdateTime()).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    void shouldNotModifyPhotoPlanWhenInputsAreNull() {
        PhotoPlan photo = new PhotoPlan();
        String initialName = "InitialName";
        photo.setName(initialName);
        Double initialX = 15.0;
        photo.setX(initialX);
        Double initialY = 25.0;
        photo.setY(initialY);
        UUID initialUuid = photo.getFileUuid();
        Plan initialPlan = photo.getPlan();
        Instant initialLastUpdateTime = photo.getLastUpdateTime();

        photoMapper.mapToPhoto(photo, null, null, null);

        assertThat(photo.getName()).isEqualTo(initialName);
        assertThat(photo.getX()).isEqualTo(initialX);
        assertThat(photo.getY()).isEqualTo(initialY);
        assertThat(photo.getFileUuid()).isEqualTo(initialUuid);
        assertThat(photo.getPlan()).isEqualTo(initialPlan);
        assertThat(photo.getLastUpdateTime()).isEqualTo(initialLastUpdateTime);
    }

    @Test
    void whenMapToPhoto_givenPhotoPlanAndCategory_shouldReturnPhoto() {
        // Arrange
        PhotoPlan photoPlan = new PhotoPlan();
        photoPlan.setName("TestName");
        photoPlan.setFileUuid(UUID.randomUUID());

        Category category = new Category();
        category.setRecommendation("TestRecommendation");

        // Act
        Photo result = photoMapper.mapToPhoto(photoPlan, category);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("TestName");
        assertThat(result.getFileUuid()).isEqualTo(photoPlan.getFileUuid());
        assertThat(result.getOriginPhoto()).isEqualTo(photoPlan);
        assertThat(result.getCategory()).isEqualTo(category);
        assertThat(result.getRecommendation()).isEqualTo("TestRecommendation");
    }

    @Test
    void whenMapToPhoto_givenNullPhotoPlanAndCategory_shouldReturnNull() {
        // Act
        Photo result = photoMapper.mapToPhoto(null, null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void whenMapToPhoto_givenPhotoPlanAndNullCategory_shouldMapPhotoPlanProperties() {
        // Arrange
        PhotoPlan photoPlan = new PhotoPlan();
        photoPlan.setName("TestName");
        photoPlan.setFileUuid(UUID.randomUUID());

        // Act
        Photo result = photoMapper.mapToPhoto(photoPlan, null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("TestName");
        assertThat(result.getFileUuid()).isEqualTo(photoPlan.getFileUuid());
        assertThat(result.getOriginPhoto()).isEqualTo(photoPlan);
        assertThat(result.getCategory()).isNull();
        assertThat(result.getRecommendation()).isNull();
    }

    @Test
    void whenMapToPhoto_givenNullPhotoPlanAndCategory_shouldMapCategoryProperties() {
        // Arrange
        Category category = new Category();
        category.setRecommendation("TestRecommendation");

        // Act
        Photo result = photoMapper.mapToPhoto(null, category);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isNull();
        assertThat(result.getFileUuid()).isNull();
        assertThat(result.getOriginPhoto()).isNull();
        assertThat(result.getCategory()).isEqualTo(category);
        assertThat(result.getRecommendation()).isEqualTo("TestRecommendation");
    }

    @Test
    void whenMapToPhotos_givenDefectDtoSet_shouldReturnDefectSet() {
        // Arrange
        Set<PhotoDefectsDto.DefectDto> defectDtoSet = new HashSet<>();
        PhotoDefectsDto.DefectDto defectDto1 = new PhotoDefectsDto.DefectDto();
        defectDto1.setDefectName("Defect1");
        defectDto1.setCoordsX(Arrays.asList(1, 2));
        defectDto1.setCoordsY(Arrays.asList(3, 4));

        PhotoDefectsDto.DefectDto defectDto2 = new PhotoDefectsDto.DefectDto();
        defectDto2.setDefectName("Defect2");
        defectDto2.setCoordsX(Arrays.asList(5, 6));
        defectDto2.setCoordsY(Arrays.asList(7, 8));

        defectDtoSet.add(defectDto1);
        defectDtoSet.add(defectDto2);

        // Act
        Set<Photo.Defect> result = photoMapper.mapToPhotos(defectDtoSet);

        // Assert
        assertThat(result).hasSize(2);

        // Verify each mapped defect
        assertThat(result).extracting("name").containsOnly("Defect1", "Defect2");
        assertThat(result).flatExtracting("coords")
                .extracting("x", "y")
                .contains(tuple(1, 3), tuple(2, 4), tuple(5, 7), tuple(6, 8));
    }

    @Test
    void whenMapToPhotos_givenNullDefectDtoSet_shouldReturnNull() {
        // Act
        Set<Photo.Defect> result = photoMapper.mapToPhotos((Set<PhotoDefectsDto.DefectDto>) null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void whenMapToPhotos_givenEmptyDefectDtoSet_shouldReturnEmptyDefectSet() {
        // Arrange
        Set<PhotoDefectsDto.DefectDto> defectDtoSet = new HashSet<>();

        // Act
        Set<Photo.Defect> result = photoMapper.mapToPhotos(defectDtoSet);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void whenMapToPhotos_givenDefectDtoWithNullCoords_shouldReturnDefectWithNullCoords() {
        // Arrange
        Set<PhotoDefectsDto.DefectDto> defectDtoSet = new HashSet<>();
        PhotoDefectsDto.DefectDto defectDto = new PhotoDefectsDto.DefectDto();
        defectDto.setDefectName("DefectWithNullCoords");
        defectDtoSet.add(defectDto);

        // Act
        Set<Photo.Defect> result = photoMapper.mapToPhotos(defectDtoSet);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getCoords()).isNull();
    }

    @Test
    void whenMapToPhotos_givenDefectDtoWithMismatchedCoords_shouldThrowException() {
        // Arrange
        Set<PhotoDefectsDto.DefectDto> defectDtoSet = new HashSet<>();
        PhotoDefectsDto.DefectDto defectDto = new PhotoDefectsDto.DefectDto();
        defectDto.setDefectName("DefectWithMismatchedCoords");
        defectDto.setCoordsX(Arrays.asList(1));
        defectDto.setCoordsY(Arrays.asList(2, 3));
        defectDtoSet.add(defectDto);

        // Act / Assert
        assertThatThrownBy(() -> photoMapper.mapToPhotos(defectDtoSet))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cant have different count of X and Y");
    }

    @Test
    void whenMapToProcessingImage_givenPhotoAndPhotoNum_shouldReturnProcessingImageDto() {
        // Arrange
        Photo photo = new Photo();
        photo.setFileUuid(UUID.randomUUID());
        photo.setId(1L);
        photo.setDefectsCoords(new HashSet<>()); // Assume this method exists and returns a Set of Defect objects
        Long photoNum = 2L;

        // Act
        ProcessingImageDto result = photoMapper.mapToProcessingImage(photo, photoNum);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo(photo.getFileUuid());
        assertThat(result.getId()).isEqualTo(photo.getId());
        assertThat(result.getPhotoNum()).isEqualTo(photoNum);
        // Assume toPhotoDefectsDto method exists and is tested elsewhere
    }

    @Test
    void whenMapToProcessingImage_givenNullPhotoAndPhotoNum_shouldReturnNull() {
        // Act
        ProcessingImageDto result = photoMapper.mapToProcessingImage(null, null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void whenMapToProcessingImage_givenUuid_shouldReturnProcessingImageDtoWithUuid() {
        UUID uuid = UUID.randomUUID();

        ProcessingImageDto result = photoMapper.mapToProcessingImage(uuid);

        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo(uuid);
    }

    @Test
    void whenMapToCkSendProcessDto_givenProcessingImageDto_shouldMapToCkImageProcessingDto() {
        ProcessingImageDto processingImageDto = new ProcessingImageDto();
        processingImageDto.setPhotoBytes(new byte[] { 1, 2, 3, 4 }); // Assume this method exists and sets the photo bytes

        CkImageProcessingDto result = photoMapper.mapToCkSendProcessDto(processingImageDto);

        assertThat(result).isNotNull();
        assertThat(result.getImgBASE64()).isEqualTo(toBase64(new byte[] { 1, 2, 3, 4 })); // Assume toBase64 method exists and is tested elsewhere
        assertThat(result.getNnMode()).isEqualTo("facade");
    }

    @Test
    void whenToDefectsDto_givenDefectSet_shouldReturnDefectDtoSet() {
        // Arrange
        Set<Photo.Defect> defects = new HashSet<>();
        defects.add(new Photo.Defect()); // Assume this method exists and adds a Defect object

        Set<PhotoDefectsDto.DefectDto> result = photoMapper.toDefectsDto(defects);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    void whenDefectToDefectDto_givenDefect_shouldReturnDefectDto() {
        Photo.Defect defect = new Photo.Defect();
        defect.setName("TestDefect");

        PhotoDefectsDto.DefectDto result = photoMapper.defectToDefectDto(defect);

        assertThat(result).isNotNull();
        assertThat(result.getDefectName()).isEqualTo("TestDefect");
    }
}
