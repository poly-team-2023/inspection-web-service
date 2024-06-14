package com.service.inspection.service;


import com.service.inspection.advice.MessageException;
import com.service.inspection.configs.BucketName;
import com.service.inspection.dto.inspection.InspectionDto;
import com.service.inspection.dto.inspection.PhotoCreateDto;
import com.service.inspection.entities.*;
import com.service.inspection.mapper.CategoryMapper;
import com.service.inspection.mapper.InspectionMapper;
import com.service.inspection.mapper.PhotoMapper;
import com.service.inspection.repositories.*;
import com.service.inspection.utils.ServiceUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class InspectionServiceTest {

    @Mock
    private InspectionRepository inspectionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private InspectionMapper inspectionMapper;
    @Mock
    private StorageService storageService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private PhotoMapper photoMapper;
    @Mock
    private ServiceUtils serviceUtils;
    @Mock
    private PhotoRepository photoRepository;
    @Mock
    private DocumentService documentService;
    @Mock
    private AnalyzeService analyzeService;
    @Mock
    private InspectionFetcherEngine inspectionFetcherEngine;
    @Mock
    private PlanRepository planRepository;
    @Mock
    private PhotoPlanRepository photoPlanRepository;

    @InjectMocks
    private InspectionService inspectionService;

    @Test
    void testCreateInspection() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Inspection inspection = new Inspection();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(inspectionRepository.save(any(Inspection.class))).thenReturn(inspection);
        when(serviceUtils.tryToFindByID(any(UserRepository.class), anyLong())).thenReturn(user);

        Identifiable result = inspectionService.createInspection(userId);

        assertThat(result).isNotNull();
        verify(inspectionRepository).save(any(Inspection.class));
    }

    @Test
    void testGetUserInspections() {
        Long userId = 1L;
        Integer pageSize = 10;
        Integer pageNum = 0;
        Page<Inspection> expectedPage = new PageImpl<>(List.of(new Inspection()));
        when(inspectionRepository.findByUsersId(eq(userId), any())).thenReturn(expectedPage);

        Page<Inspection> result = inspectionService.getUserInspections(userId, pageSize, pageNum);

        assertThat(result).isEqualTo(expectedPage);

        verify(inspectionRepository).findByUsersId(userId, PageRequest.of(pageNum, pageSize));
    }

    @Test
    void testDeleteInspection() {
        Long userId = 1L;
        Long inspectionId = 2L;
        Inspection inspection = new Inspection();
        inspection.setId(2L);
        when(inspectionRepository.findByUsersIdAndId(userId, inspectionId)).thenReturn(inspection);

        inspectionService.deleteInspection(inspectionId, userId);

        verify(inspectionRepository).deleteById(inspectionId);
    }

    @Test
    void testUpdateInspection() {
        Long userId = 1L;
        Long inspectionId = 2L;
        InspectionDto inspectionDto = new InspectionDto();
        Inspection inspection = new Inspection();
        inspection.setId(inspectionId);

        when(inspectionRepository.findByUsersIdAndId(userId, inspectionId)).thenReturn(inspection);

        inspectionService.updateInspection(inspectionId, userId, inspectionDto);

        verify(inspectionMapper).mapToInspection(inspection, inspectionDto);
        verify(inspectionRepository).save(inspection);
    }

    @Test
    void testUploadMainInspectionPhoto() {
        MockMultipartFile mockMultipartFile =
                new MockMultipartFile("test", "test-original", MediaType.IMAGE_PNG_VALUE, "test-original".getBytes());
        Long userId = 1L;
        Long inspectionId = 2L;

        Inspection inspection = new Inspection();
        inspection.setId(inspectionId);
        when(inspectionRepository.findByUsersIdAndId(userId, inspectionId)).thenReturn(inspection);

        inspectionService.uploadMainInspectionPhoto(userId, inspectionId, mockMultipartFile);

        verify(inspectionRepository).save(inspection);
        verify(storageService).saveFile(eq(BucketName.INSPECTION_MAIN_PHOTO), anyString(), eq(mockMultipartFile));

        assertThat(inspection.getMainPhotoName()).isEqualTo(mockMultipartFile.getOriginalFilename());
        assertThat(inspection.getMainPhotoUuid()).isNotNull();
    }

    @Test
    void testGetMainInspectionPhoto() {
        Long userId = 1L;
        Long inspectionId = 2L;
        UUID uuid = UUID.randomUUID();

        Inspection inspection = new Inspection();
        inspection.setId(inspectionId);
        inspection.setMainPhotoUuid(uuid);

        when(inspectionRepository.findByUsersIdAndId(userId, inspectionId)).thenReturn(inspection);

        inspectionService.getMainInspectionPhoto(inspectionId, userId);

        verify(storageService).getFile(eq(BucketName.INSPECTION_MAIN_PHOTO), eq(uuid.toString()));
    }

    @Test
    void testGetMainInspectionPhotoThenNull() {
        Long userId = 1L;
        Long inspectionId = 2L;

        Inspection inspection = new Inspection();
        inspection.setId(inspectionId);

        when(inspectionRepository.findByUsersIdAndId(userId, inspectionId)).thenReturn(inspection);

        assertThat(inspectionService.getMainInspectionPhoto(inspectionId, userId)).isNull();

        verify(storageService, times(0)).getFile(any(BucketName.class), anyString());
    }

    @Test
    void testAddPlanToInspection() {
        Long userId = 1L;
        Long inspectionId = 2L;
        String name = "Plan Name";
        UUID uuid = UUID.randomUUID();

        MockMultipartFile multipartFile = new MockMultipartFile("test", "test-original", MediaType.IMAGE_PNG_VALUE, "test-original".getBytes());

        Inspection inspection = new Inspection();
        inspection.setId(inspectionId);

        Plan expected = new Plan();
        expected.setId(3L);
        expected.setInspection(inspection);
        expected.setName(name);
        expected.setFileUuid(uuid);

        when(inspectionRepository.findByUsersIdAndId(userId, inspectionId)).thenReturn(inspection);
        when(planRepository.save(any())).thenAnswer((Answer<Plan>) invocation -> {
            Plan plan = invocation.getArgument(0);
            plan.setId(3L);
            return plan;
        });


        Identifiable result = inspectionService.addPlanToInspection(userId, inspectionId, name, multipartFile);

        assertThat(result.getId()).isEqualTo(expected.getId());

        verify(planRepository).save(any(Plan.class));
        verify(storageService).saveFile(any(BucketName.class), anyString(), eq(multipartFile));
    }


    @Test
    public void testDeletePlanFromInspection() {
        Long userId = 1L;
        Long inspectionId = 2L;
        Long planId = 3L;

        Inspection inspection = new Inspection();
        Plan plan = new Plan();
        plan.setId(planId);
        Set<Plan> plans = new HashSet<>();
        plans.add(plan);
        inspection.setPlans(plans);

        when(inspectionRepository.findByUsersIdAndId(userId, inspectionId)).thenReturn(inspection);
        when(serviceUtils.tryToFindByID(any(Collection.class), eq(planId))).thenReturn(plan);
        doNothing().when(planRepository).delete(plan);

        inspectionService.deletePlanFromInspection(userId, inspectionId, planId);

        verify(inspectionRepository).findByUsersIdAndId(userId, inspectionId);
        verify(planRepository).delete(plan);

        assertThat(inspection.getPlans()).isEmpty();
    }

    @Test
    void testUpdatePlanFromInspection() {
        Long userId = 1L;
        Long inspectionId = 2L;
        Long planId = 3L;
        String newName = "new name";

        Inspection inspection = new Inspection();
        Plan plan = new Plan();
        plan.setId(planId);

        Set<Plan> plans = new HashSet<>();
        plans.add(plan);
        inspection.setPlans(plans);

        Plan expected = new Plan();
        expected.setId(planId);
        expected.setName(newName);

        when(inspectionRepository.findByUsersIdAndId(userId, inspectionId)).thenReturn(inspection);
        when(serviceUtils.tryToFindByID(any(Collection.class), eq(planId))).thenReturn(plan);

        inspectionService.updatePlanFromInspection(userId, inspectionId, planId, newName);

        verify(inspectionRepository).findByUsersIdAndId(userId, inspectionId);
        verify(planRepository).save(expected);
    }

    @Test
    void testUpdateOrCreatePhoto() {
        Long userId = 1L;
        Long inspectionId = 2L;
        Long photoId = 2L;
        Long planId = 2L;

        PhotoPlan photoPlan = createPhotoPlanWithUser(userId);
        photoPlan.setFileUuid(UUID.randomUUID());

        PhotoCreateDto photoCreateDto = new PhotoCreateDto();

        when(photoPlanRepository.findById(photoId)).thenReturn(Optional.of(photoPlan));
        doNothing().when(photoMapper).mapToPhoto(photoPlan, photoCreateDto, photoPlan.getFileUuid(), planId);

        inspectionService.updateOrCreatePhoto(userId, photoId, planId, photoCreateDto, null);

        verify(storageService, times(0)).saveFile(any(BucketName.class), any(UUID.class), any());
    }

    @Test
    void testUpdateOrCreatePhotoFaild() {
        Long userId = 1L;
        Long planId = 2L;

        PhotoCreateDto photoCreateDto = new PhotoCreateDto();
        MockMultipartFile multipartFile = new MockMultipartFile("123", (byte[]) null);
        assertThatThrownBy(() -> inspectionService.updateOrCreatePhoto(userId, null, planId, photoCreateDto, null))
                .isExactlyInstanceOf(MessageException.class);

        assertThatThrownBy(() -> inspectionService.updateOrCreatePhoto(userId, null, planId, photoCreateDto, multipartFile))
                .isExactlyInstanceOf(MessageException.class);
    }

    @Test
    void testUpdateOrCreatePhotoFaild2() {
        Long userId = 1L;
        Long photoId = 2L;
        Long planId = 2L;


        when(photoPlanRepository.findById(photoId)).thenReturn(Optional.ofNullable(null));
        assertThatThrownBy(() -> inspectionService.updateOrCreatePhoto(userId, photoId, planId, null, null))
                .isExactlyInstanceOf(EntityNotFoundException.class);
    }


    @Test
    void testUpdateOrCreatePhotoCreate() {
        Long userId = 1L;
        Long photoId = 2L;
        Long planId = 2L;

        MockMultipartFile mockMultipartFile =
                new MockMultipartFile("test", "test-original", MediaType.IMAGE_PNG_VALUE, "test-original".getBytes());

        PhotoPlan photoPlan = createPhotoPlanWithUser(userId);
        photoPlan.setFileUuid(UUID.randomUUID());

        PhotoCreateDto photoCreateDto = new PhotoCreateDto();

        when(photoPlanRepository.findById(photoId)).thenReturn(Optional.of(photoPlan));
        doNothing().when(photoMapper).mapToPhoto(photoPlan, photoCreateDto, photoPlan.getFileUuid(), planId);

        inspectionService.updateOrCreatePhoto(userId, null, planId, photoCreateDto, mockMultipartFile);

        verify(storageService).saveFile(any(BucketName.class), any(UUID.class), any());
        verify(photoPlanRepository).save(any());
    }

    @Test
    void testGetPhotoFromPlan() {
        Long userId = 1L;
        Long photoId = 2L;
        Long planId = 2L;

        UUID uuid = UUID.randomUUID();

        PhotoPlan photoPlan = createPhotoPlanWithUser(userId);
        photoPlan.setFileUuid(uuid);

        StorageService.BytesWithContentType expected = new StorageService.BytesWithContentType(null, null);

        when(photoPlanRepository.findById(photoId)).thenReturn(Optional.of(photoPlan));
        when(storageService.getFile(BucketName.DEFAULT_IMAGE_BUCKET, uuid.toString())).thenReturn(expected);

        assertThat(inspectionService.getPhotoFromPlan(userId, 1L, planId, photoId)).isEqualTo(expected);
    }


    @Test
    void testDeletePhotoFromPlan() {
        Long userId = 1L;
        Long photoId = 2L;

        PhotoPlan photoPlan = createPhotoPlanWithUser(userId);

        when(photoPlanRepository.findById(photoId)).thenReturn(Optional.of(photoPlan));

        inspectionService.deletePhotoFromPlan(userId, photoId);

        verify(photoPlanRepository).delete(photoPlan);
    }

    @Test
    void testDeletePhotoFromPlanFaild() {
        Long userId = 1L;
        Long photoId = 2L;

        PhotoPlan photoPlan = new PhotoPlan();

        when(photoPlanRepository.findById(photoId)).thenReturn(Optional.of(photoPlan));

        inspectionService.deletePhotoFromPlan(userId, photoId);

        verify(photoPlanRepository, times(0)).delete(photoPlan);
    }

    @Test
    void createNewCategory() {
        Long userId = 1L;
        Long inspectionId = 2L;
        String categoryName = "categoryName";

        Inspection inspection = new Inspection();
        inspection.setId(2L);

        Category category = new Category();
        category.setName(categoryName);
        category.setId(4L);

        when(inspectionRepository.findByUsersIdAndId(userId, inspectionId)).thenReturn(inspection);
        when(categoryMapper.mapToCategory(eq(categoryName), eq(inspection))).thenReturn(category);

        Identifiable result = inspectionService.createNewCategory(userId, inspectionId, categoryName);

        assertThat(result.getId()).isEqualTo(category.getId());
        verify(inspectionRepository).save(inspection);
    }

    @Test
     void getAllCategories() {
        Long userId = 1L;
        Long inspectionId = 2L;
        String categoryName = "categoryName";

        Inspection inspection = new Inspection();
        inspection.setId(2L);

        Category category = new Category();
        category.setName(categoryName);
        category.setId(4L);

        Category category1 = new Category();
        category1.setName(categoryName);
        category1.setId(4L);

        inspection.setCategories(List.of(category, category1));

        when(inspectionRepository.findByUsersIdAndId(userId, inspectionId)).thenReturn(inspection);

        List<Category> result = inspectionService.getAllCategories(inspectionId, userId);

        assertThat(result).isEqualTo(inspection.getCategories());
    }

    private PhotoPlan createPhotoPlanWithUser(Long userId) {
        PhotoPlan photoPlan = new PhotoPlan();
        Plan plan = new Plan();
        photoPlan.setPlan(plan);
        Inspection inspection = new Inspection();
        plan.setInspection(inspection);
        User user = new User();
        user.setId(userId);
        inspection.setUsers(Set.of(user));
        return photoPlan;
    }
}
