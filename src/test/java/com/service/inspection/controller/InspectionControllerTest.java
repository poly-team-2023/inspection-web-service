package com.service.inspection.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.service.inspection.configs.security.jwt.AuthTokenFilter;
import com.service.inspection.dto.IdentifiableDto;
import com.service.inspection.dto.document.DocumentStatusDto;
import com.service.inspection.dto.inspection.*;
import com.service.inspection.entities.*;
import com.service.inspection.entities.enums.ProgressingStatus;
import com.service.inspection.mapper.*;
import com.service.inspection.service.InspectionService;
import com.service.inspection.service.StorageService;
import com.service.inspection.utils.ControllerUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

@WebMvcTest(controllers = InspectionController.class,
        excludeFilters = @ComponentScan.Filter(classes = AuthTokenFilter.class, type = FilterType.ASSIGNABLE_TYPE))
@WithMockUser
class InspectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InspectionService inspectionService;
    @MockBean
    private InspectionMapper inspectionMapper;

    @MockBean
    private ControllerUtils utils;

    @MockBean
    private CommonMapper commonMapper;
    @MockBean
    private CategoryMapper categoryMapper;
    @MockBean
    private PlanMapper planMapper;
    @MockBean
    private PhotoMapper photoMapper;

    @BeforeEach
    public void setUp() {
        when(utils.getUserId(any())).thenReturn(1L);
    }

    @Test
    void testStart() throws Exception {

        when(inspectionService.createInspection(any())).thenReturn(new Identifiable(1L));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/inspections")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllInspections() throws Exception {
        // Подготовка данных
        int pageNum = 0;
        int pageSize = 10;
        Inspection inspection = new Inspection(); // Создайте экземпляр вашего класса Inspection
        InspectionWithName inspectionWithName = new InspectionWithName(); // Создайте экземпляр вашего класса InspectionWithName
        List<Inspection> inspectionsList = Collections.singletonList(inspection);
        Page<Inspection> inspectionsPage = new PageImpl<>(inspectionsList);

        // Настройка поведения моков
        when(inspectionService.getUserInspections(1L, pageSize, pageNum)).thenReturn(inspectionsPage);
        when(inspectionMapper.mapToInspectionWithName(any(Inspection.class))).thenReturn(inspectionWithName);

        // Выполнение запроса и проверка результатов
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/inspections")
                        .param("pageNum", String.valueOf(pageNum))
                        .param("pageSize", String.valueOf(pageSize))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    @Test
    void testDeleteInspection() throws Exception {
        Long inspectionId = 1L;
        doNothing().when(inspectionService).deleteInspection(inspectionId, 1L);

        mockMvc.perform(delete("/api/v1/inspections/{id}", inspectionId)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(inspectionService).deleteInspection(inspectionId, 1L);
    }

    @Test
    void testUpdateInspection() throws Exception {
        Long inspectionId = 1L;
        InspectionDto inspectionDto = new InspectionDto(); // Создайте экземпляр вашего DTO
        // Настройте поля inspectionDto по необходимости

        doNothing().when(inspectionService).updateInspection(inspectionId, 1L, inspectionDto);

        mockMvc.perform(put("/api/v1/inspections/{id}", inspectionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(inspectionDto))
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(inspectionService).updateInspection(inspectionId, 1L, inspectionDto);
    }

    @Test
    void testUploadMainPhoto() throws Exception {
        Long inspectionId = 1L;
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        doNothing().when(inspectionService).uploadMainInspectionPhoto(1L, inspectionId, mockMultipartFile);

        mockMvc.perform(multipart("/api/v1/inspections/{id}/main-photo", inspectionId)
                        .file(mockMultipartFile)
                        .with(csrf())).andExpect(status().isOk());

        verify(inspectionService).uploadMainInspectionPhoto(1L, inspectionId, mockMultipartFile);
    }

    @Test
    void testGetInspectionMainPhoto() throws Exception {
        Long inspectionId = 1L;
        StorageService.BytesWithContentType file = new StorageService.BytesWithContentType(
                "image content".getBytes(),
                MediaType.IMAGE_JPEG_VALUE
        );

        when(inspectionService.getMainInspectionPhoto(inspectionId, 1L)).thenReturn(file);
        when(utils.getResponseEntityFromFile(anyString(), any(StorageService.BytesWithContentType.class)))
                .thenCallRealMethod();

        mockMvc.perform(get("/api/v1/inspections/{id}/main-photo", inspectionId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE));

        verify(inspectionService).getMainInspectionPhoto(inspectionId, 1L);
    }

    @Test
    void testCreateNewCategory() throws Exception {
        Long inspectionId = 1L;
        String categoryName = "Test Category";
        IdentifiableDto identifiableDto = new IdentifiableDto(1L);

        Category category = new Category();
        category.setName(categoryName);
        category.setId(1L);

        when(inspectionService.createNewCategory(1L, inspectionId, categoryName)).thenReturn(category);
        when(commonMapper.mapToIdentifiableDto(any(Category.class))).thenReturn(identifiableDto);

        mockMvc.perform(post("/api/v1/inspections/{id}/categories", inspectionId)
                        .param("name", categoryName)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(identifiableDto.getId()));

        verify(inspectionService).createNewCategory(1L, inspectionId, categoryName);
    }

    @Test
    void testGetAllCategories() throws Exception {
        Long inspectionId = 1L;

        CategoryWithFile categoryWithFile = new CategoryWithFile();
        categoryWithFile.setName("Test Category");
        categoryWithFile.setId(1L);
        categoryWithFile.setPhotos(Collections.emptySet());

        Category category = new Category();
        category.setName(categoryWithFile.getName());
        category.setId(1L);

        List<CategoryWithFile> categoryWithFiles = Collections.singletonList(categoryWithFile);

        when(inspectionService.getAllCategories(inspectionId, 1L)).thenReturn(Collections.singletonList(category));
        when(categoryMapper.mapToCategoryWithFile(anyList())).thenReturn(categoryWithFiles);

        mockMvc.perform(get("/api/v1/inspections/{id}/categories", inspectionId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(categoryWithFiles.get(0).getId()));

        verify(inspectionService).getAllCategories(inspectionId, 1L);
    }

    @Test
    void testUpdateCategory() throws Exception {
        Long inspectionId = 1L;
        Long categoryId = 2L;
        String categoryName = "Updated Category Name";

        doNothing().when(inspectionService).updateCategory(1L, categoryId, inspectionId, categoryName);

        mockMvc.perform(put("/api/v1/inspections/{id}/categories/{categoryId}", inspectionId, categoryId)
                        .param("name", categoryName)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(inspectionService).updateCategory(1L, categoryId, inspectionId, categoryName);
    }

    @Test
    void testDeleteCategory() throws Exception {
        Long inspectionId = 1L;
        Long categoryId = 2L;

        doNothing().when(inspectionService).deleteCategory(1L, inspectionId, categoryId);

        mockMvc.perform(delete("/api/v1/inspections/{id}/categories/{categoryId}", inspectionId, categoryId)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(inspectionService).deleteCategory(1L, inspectionId, categoryId);
    }


    @Test
    void testAddPhotoToCategory() throws Exception {
        Long categoryId = 1L;
        Long id = 2L;
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());

        Identifiable identifiable = new Identifiable(3L);
        when(inspectionService.addPhotoToCategory(1L, id, categoryId, mockMultipartFile)).thenReturn(identifiable);
        when(commonMapper.mapToIdentifiableDto(identifiable)).thenReturn(new IdentifiableDto(identifiable.getId()));

        mockMvc.perform(multipart("/api/v1/inspections/{id}/categories/{categoryId}/photos", id, categoryId)
                        .file(mockMultipartFile)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(identifiable.getId()));
    }

    @Test
    void testDeletePhotoFromCategory() throws Exception {
        Long categoryId = 1L;
        Long id = 2L;
        Long photoId = 3L;

        doNothing().when(inspectionService).deletePhoto(1L, id, categoryId, photoId);

        mockMvc.perform(delete("/api/v1/inspections/{id}/categories/{categoryId}/photos/{photoId}", id, categoryId, photoId)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCategoryPhoto() throws Exception {
        Long categoryId = 1L;
        Long id = 2L;
        Long photoId = 3L;
        StorageService.BytesWithContentType file = new StorageService.BytesWithContentType(
                "image content".getBytes(), MediaType.IMAGE_JPEG_VALUE);

        when(inspectionService.getCategoryPhoto(1L, id, categoryId, photoId)).thenReturn(file);
        when(utils.getResponseEntityFromFile(anyString(), any(StorageService.BytesWithContentType.class)))
                .thenCallRealMethod();

        mockMvc.perform(get("/api/v1/inspections/{id}/categories/{categoryId}/photos/{photoId}", id, categoryId, photoId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE));
    }

    @Test
    void testSendPhotosAnalyze() throws Exception {
        Long id = 1L;

        doNothing().when(inspectionService).sendAllPhotosToAnalyze(id, 1L);

        mockMvc.perform(post("/api/v1/inspections/{id}/categories/process", id)
                        .with(csrf()))
                .andExpect(status().isOk());
    }


    @Test
    void testAddPlanToInspection() throws Exception {
        Long id = 1L;
        String name = "Test Plan";
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file", "plan.pdf", MediaType.APPLICATION_PDF_VALUE, "test plan content".getBytes());

        Identifiable identifiable = new Identifiable(2L);
        when(inspectionService.addPlanToInspection(1L, id, name, mockMultipartFile)).thenReturn(identifiable);
        when(commonMapper.mapToIdentifiableDto(identifiable)).thenReturn(new IdentifiableDto(identifiable.getId()));

        mockMvc.perform(multipart("/api/v1/inspections/{id}/plans", id)
                        .file(mockMultipartFile)
                        .param("name", name)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(identifiable.getId()));
    }

    @Test
    void testDeletePlanFromInspection() throws Exception {
        Long id = 1L;
        Long planId = 2L;

        doNothing().when(inspectionService).deletePlanFromInspection(1L, id, planId);

        mockMvc.perform(delete("/api/v1/inspections/{id}/plans/{planId}", id, planId)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllPlans() throws Exception {
        Long id = 1L;
        InspectionPlansDto inspectionPlansDto = new InspectionPlansDto(); // Assume this is a valid DTO for your response

        when(inspectionService.getPlans(1L, id)).thenReturn(Collections.emptySet()); // Assume this returns a valid list of plans
        when(planMapper.mapToInspectionPlanDto(anySet())).thenReturn(inspectionPlansDto);

        mockMvc.perform(get("/api/v1/inspections/{id}/plans", id)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetPhotosOnPlan() throws Exception {
        Long id = 1L;
        Long planId = 2L;
        PlanDto planDto = new PlanDto(); // Assume this is a valid DTO for your response

        when(inspectionService.getFullPlanInfo(1L, id, planId)).thenReturn(new Plan()); // Assume this returns a valid Plan object
        when(planMapper.mapToPlanDto(any(Plan.class))).thenReturn(planDto);

        mockMvc.perform(get("/api/v1/inspections/{id}/plans/{planId}", id, planId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetPlanFile() throws Exception {
        Long id = 1L;
        Long planId = 2L;
        StorageService.BytesWithContentType file = new StorageService.BytesWithContentType(
                "plan content".getBytes(), MediaType.APPLICATION_PDF_VALUE);

        when(inspectionService.getPlanFile(1L, id, planId)).thenReturn(file);
        when(utils.getResponseEntityFromFile(anyString(), any(StorageService.BytesWithContentType.class)))
                .thenCallRealMethod();

        mockMvc.perform(get("/api/v1/inspections/{id}/plans/{planId}/file", id, planId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF_VALUE));
    }

    @Test
    void testSavePhoto() throws Exception {
        Long id = 1L;
        Long planId = 2L;
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file", "photo.jpg", MediaType.IMAGE_JPEG_VALUE, "photo content".getBytes());
        PhotoCreateDto photoCreateDto = new PhotoCreateDto();
        photoCreateDto.setName("Test Photo");
        photoCreateDto.setX(1.0d);
        photoCreateDto.setY(2.0d);

        when(inspectionService.updateOrCreatePhoto(1L, null, planId, photoCreateDto, mockMultipartFile))
                .thenReturn(new PhotoPlan());
        when(photoMapper.mapToPhotoCreateDto(any())).thenReturn(photoCreateDto);

        mockMvc.perform(multipart("/api/v1/inspections/{id}/plans/{planId}/photos", id, planId)
                        .file(mockMultipartFile)
                        .param("data", asJsonString(photoCreateDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(photoCreateDto.getName()))
                .andExpect(jsonPath("$.x").value(photoCreateDto.getX()))
                .andExpect(jsonPath("$.y").value(photoCreateDto.getY()));
    }

    @Test
    void testUpdatePhoto() throws Exception {
        Long id = 1L;
        Long planId = 2L;
        Long photoId = 3L;
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file", "photo.jpg", MediaType.IMAGE_JPEG_VALUE, "updated photo content".getBytes());
        PhotoCreateDto photoCreateDto = new PhotoCreateDto();
        photoCreateDto.setId(1L);
        photoCreateDto.setName("Test Photo");
        photoCreateDto.setX(1.0d);
        photoCreateDto.setY(2.0d);

        when(inspectionService.updateOrCreatePhoto(1L, photoId, planId, photoCreateDto, mockMultipartFile))
                .thenReturn(new PhotoPlan());
        when(photoMapper.mapToPhotoCreateDto(any())).thenReturn(photoCreateDto);

        mockMvc.perform(multipart("/api/v1/inspections/{id}/plans/{planId}/photos/{photoId}", id, planId, photoId)
                .file(mockMultipartFile).param("data", asJsonString(photoCreateDto)).with(csrf())
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                })).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(photoCreateDto.getId()))
                .andExpect(jsonPath("$.name").value(photoCreateDto.getName()))
                .andExpect(jsonPath("$.x").value(photoCreateDto.getX()))
                .andExpect(jsonPath("$.y").value(photoCreateDto.getY()));
    }

    @Test
    void testGetPhoto() throws Exception {
        Long id = 1L;
        Long planId = 2L;
        Long photoId = 3L;
        StorageService.BytesWithContentType file = new StorageService.BytesWithContentType(
                "photo content".getBytes(), MediaType.IMAGE_JPEG_VALUE);

        when(inspectionService.getPhotoFromPlan(1L, id, planId, photoId)).thenReturn(file);
        when(utils.getResponseEntityFromFile(anyString(), any(StorageService.BytesWithContentType.class)))
                .thenCallRealMethod();

        mockMvc.perform(get("/api/v1/inspections/{id}/plans/{planId}/photos/{photoId}", id, planId, photoId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE));
    }

    @Test
    void testDeletePhoto() throws Exception {
        Long id = 1L;
        Long planId = 2L;
        Long photoId = 3L;

        doNothing().when(inspectionService).deletePhotoFromPlan(1L, photoId);

        mockMvc.perform(delete("/api/v1/inspections/{id}/plans/{planId}/photos/{photoId}", id, planId, photoId)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testMovePhotoFromPlanToCategory() throws Exception {
        Long id = 1L;
        Long planId = 2L;
        Long photoId = 3L;
        Long categoryId = 4L;

        when(inspectionService.copyPhotoToCategoryFromPlan(1L, id, planId, photoId, categoryId))
                .thenReturn(new Photo());
        when(commonMapper.mapToIdentifiableDto(any(Photo.class))).thenReturn(new IdentifiableDto(photoId));

        mockMvc.perform(post("/api/v1/inspections/{id}/plans/{planId}/photos/{photoId}/category/{categoryId}", id, planId, photoId, categoryId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(photoId));
    }


    @Test
    void testGetInspectionInfo() throws Exception {
        Long id = 1L;
        GetInspectionDto getInspectionDto = new GetInspectionDto();
        getInspectionDto.setId(id);
        getInspectionDto.setName("Test Inspection");

        when(inspectionService.getUserInspection(1L, id)).thenReturn(new Inspection());
        when(inspectionMapper.mapToGetInspectionDto(any(Inspection.class))).thenReturn(getInspectionDto);

        mockMvc.perform(get("/api/v1/inspections/{id}", id)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getInspectionDto.getId()))
                .andExpect(jsonPath("$.name").value(getInspectionDto.getName()));
    }

    @Test
    void testAddTaskForCreatingDocument() throws Exception {
        Long id = 1L;


        when(inspectionService.addTaskForCreatingDocument(id, 1L)).thenReturn(true);

        mockMvc.perform(post("/api/v1/inspections/{id}/docx", id)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetInspectionReport() throws Exception {
        Long id = 1L;
        StorageService.BytesWithContentType file = new StorageService.BytesWithContentType(
                "report content".getBytes(), "report.docx");

        when(inspectionService.getUserInspectionReport(id, 1L)).thenReturn(file);

        mockMvc.perform(get("/api/v1/inspections/{id}/docx", id)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .andExpect(header().exists("Content-Disposition"));
    }

    @Test
    void testGetInspectionReportStatus() throws Exception {
        Long id = 1L;
        DocumentStatusDto documentStatusDto = new DocumentStatusDto(ProgressingStatus.READY);

        when(inspectionService.getReportStatus(id, 1L)).thenReturn(ProgressingStatus.READY);

        mockMvc.perform(get("/api/v1/inspections/{id}/docx/status", id)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progressingStatus").value(documentStatusDto.getProgressingStatus().toString()));
    }


    // Вспомогательный метод для преобразования объекта в JSON строку
    static String asJsonString(final Object obj) {
        try {
            return JsonMapper.builder().addModule(new JavaTimeModule()).build().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
