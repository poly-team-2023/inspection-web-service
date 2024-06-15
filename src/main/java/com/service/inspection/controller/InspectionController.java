package com.service.inspection.controller;

import com.google.common.base.Joiner;
import com.service.inspection.dto.IdentUuid;
import com.service.inspection.dto.IdentifiableDto;
import com.service.inspection.dto.document.DocumentStatusDto;
import com.service.inspection.dto.inspection.*;
import com.service.inspection.entities.Category;
import com.service.inspection.entities.Identifiable;
import com.service.inspection.entities.Inspection;
import com.service.inspection.mapper.*;
import com.service.inspection.service.InspectionService;
import com.service.inspection.service.StorageService;
import com.service.inspection.utils.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/inspections")
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;
    private final InspectionMapper inspectionMapper;
    private final ControllerUtils utils;
    private final CommonMapper commonMapper;
    private final CategoryMapper categoryMapper;
    private final PlanMapper planMapper;
    private final PhotoMapper photoMapper;

    // TODO заменить обращение к бд для поиска пользователя с email на id для более быстрого поиска

    @PostMapping
    @Operation(summary = "Создание пустой инспекции Без названия")
    public ResponseEntity<InspectionWithIdOnly> createInspection(Authentication authentication) {
        Long userId = utils.getUserId(authentication);
        Identifiable inspection = inspectionService.createInspection(userId);

        InspectionWithIdOnly inspectionOnlyIdDto = new InspectionWithIdOnly();
        inspectionOnlyIdDto.setId(inspection.getId());

        return ResponseEntity.ok().body(inspectionOnlyIdDto);
    }

    @GetMapping
    @Operation(summary = "Получение всех инспекций")
    public ResponseEntity<Page<InspectionWithName>> getAllInspections(
            @RequestParam(defaultValue = "0", required = false) Integer pageNum,
            @RequestParam(defaultValue = "10", required = false) Integer pageSize,
            Authentication authentication
    ) {
        Long id = utils.getUserId(authentication);
        Page<Inspection> page = inspectionService.getUserInspections(id, pageSize, pageNum);

        return ResponseEntity.ok().body(
                page.map(inspectionMapper::mapToInspectionWithName)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление инспекции")
    public ResponseEntity<Void> deleteInspection(
            @PathVariable @Min(1) Long id, Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        inspectionService.deleteInspection(id, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление полей инспекции")
    public ResponseEntity<Void> updateInspection(@PathVariable @Min(1) Long id,
                                                 @RequestBody InspectionDto inspectionDto,
                                                 Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        inspectionService.updateInspection(id, userId, inspectionDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/main-photo")
    @Operation(summary = "Загрузка обложки\\главное фотографии проекта\\отчета")
    public ResponseEntity<Void> uploadMainPhoto(
            @PathVariable @Min(1) Long id,
            @RequestParam("file") MultipartFile multipartFile, Authentication authentication) {
        Long userId = utils.getUserId(authentication);
        inspectionService.uploadMainInspectionPhoto(userId, id, multipartFile);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/main-photo")
    @Operation(summary = "Скачать главную фотографию отчета")
    public ResponseEntity<Resource> getInspectionMainPhoto(
            @PathVariable @Min(1) Long id,
            Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        StorageService.BytesWithContentType file = inspectionService.getMainInspectionPhoto(id, userId);
        return utils.getResponseEntityFromFile("main-photo", file);
    }

    // ------------------------------------------------- Категории -------------------------------------------------

    @PostMapping("/{id}/categories")
    @Operation(summary = "Создание категории")
    public ResponseEntity<IdentifiableDto> createNewCategory(
            @PathVariable @Min(1) Long id, @RequestParam(name = "name") String categoryName,
            Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        return ResponseEntity.ok(
                commonMapper.mapToIdentifiableDto(inspectionService.createNewCategory(userId, id, categoryName))
        );
    }

    @GetMapping("/{id}/categories")
    @Operation(summary = "Получение всех категорий инспекций с информацией о фотографиях")
    public ResponseEntity<List<CategoryWithFile>> getAllCategories(
            Authentication authentication, @PathVariable @Min(1) Long id
    ) {
        Long userId = utils.getUserId(authentication);
        List<Category> categories = inspectionService.getAllCategories(id, userId);

        return ResponseEntity.ok(categoryMapper.mapToCategoryWithFile(categories));
    }

    @PutMapping("/{id}/categories/{categoryId}")
    @Operation(summary = "Обновление название категории")
    public ResponseEntity<Void> updateCategory(
            @PathVariable @Min(1) Long id, @RequestParam(name = "name") String categoryName,
            @PathVariable @Min(1) Long categoryId, Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        inspectionService.updateCategory(userId, categoryId, id, categoryName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/categories/{categoryId}")
    @Operation(summary = "Удаление категории")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable @Min(1) Long id, @PathVariable @Min(1) Long categoryId,
            Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        inspectionService.deleteCategory(userId, id, categoryId);
        return ResponseEntity.ok().build();
    }

    // ------------------------------------------------- Фотографии -------------------------------------------------

    @PostMapping("/{id}/categories/{categoryId}/photos")
    @Operation(summary = "Добавление фотографии категории")
    public ResponseEntity<IdentifiableDto> addPhotoToCategory(
            @PathVariable @Min(1) Long categoryId, @PathVariable @Min(1) Long id,
            @RequestParam("file") MultipartFile multipartFile, Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        Identifiable ident = inspectionService.addPhotoToCategory(userId, id, categoryId, multipartFile);
        return ResponseEntity.ok(commonMapper.mapToIdentifiableDto(ident));
    }

    @DeleteMapping("/{id}/categories/{categoryId}/photos/{photoId}")
    @Operation(summary = "Удаление фотографий")
    public ResponseEntity<Void> deletePhotoFromCategory(
            @PathVariable @Min(1) Long categoryId, @PathVariable @Min(1) Long id,
            @PathVariable @Min(1) Long photoId, Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        inspectionService.deletePhoto(userId, id, categoryId, photoId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/categories/{categoryId}/photos/{photoId}")
    @Operation(summary = "Получение фотографии")
    public ResponseEntity<Resource> getCategoryPhoto(
            @PathVariable @Min(1) Long categoryId, @PathVariable @Min(1) Long id,
            @PathVariable @Min(1) Long photoId, Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        StorageService.BytesWithContentType file = inspectionService.getCategoryPhoto(userId, id, categoryId, photoId);
        return utils.getResponseEntityFromFile("category-photo", file);
    }

    @PostMapping("/{id}/categories/process")
    @Operation(summary = "Отправить загруженные фотографии на анализ")
    public ResponseEntity<Void> sendPhotosAnalyze(@PathVariable @Min(1) Long id, Authentication authentication) {
        Long userId = utils.getUserId(authentication);
        inspectionService.sendAllPhotosToAnalyze(id, userId);
        return ResponseEntity.ok().build();
    }

    // ------------------------------------------------- Чертеж -------------------------------------------------

    @Tag(name = "Мобильное приложение", description = "APIs для мобильного приложения")
    @PostMapping("/{id}/plans")
    @Operation(summary = "Добавление чертежа к проекту")
    public ResponseEntity<IdentifiableDto> addPlanToInspection(
            @PathVariable @Min(1) Long id, @RequestParam("name") String name,
            @RequestParam("file") MultipartFile multipartFile, Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        Identifiable ident = inspectionService.addPlanToInspection(userId, id, name, multipartFile);
        return ResponseEntity.ok(commonMapper.mapToIdentifiableDto(ident));
    }

    @Tag(name = "Мобильное приложение", description = "APIs для мобильного приложения")
    @DeleteMapping("/{id}/plans/{planId}")
    @Operation(summary = "Удаление чертежа из проекта")
    public ResponseEntity<Void> deletePlanFromInspection(
            @PathVariable @Min(1) Long planId, @PathVariable @Min(1) Long id, Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        inspectionService.deletePlanFromInspection(userId, id, planId);
        return ResponseEntity.ok().build();
    }

    @Tag(name = "Мобильное приложение", description = "APIs для мобильного приложения")
    @GetMapping("/{id}/plans")
    @Operation(summary = "Получение информации о чертежах")
    public ResponseEntity<InspectionPlansDto> getAllPlans(
            @PathVariable @Min(1) Long id, Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        return ResponseEntity.ok(planMapper.mapToInspectionPlanDto(inspectionService.getPlans(userId, id)));
    }

    @Tag(name = "Мобильное приложение", description = "APIs для мобильного приложения")
    @GetMapping("/{id}/plans/{planId}")
    @Operation(summary = "Информация о фотография на чертежах")
    public ResponseEntity<PlanDto> getPhotosOnPlan(
            @PathVariable @Min(1) Long id, @PathVariable @Min(1) Long planId, Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        return ResponseEntity.ok(planMapper.mapToPlanDto(inspectionService.getFullPlanInfo(userId, id, planId)));
    }

    @Tag(name = "Мобильное приложение", description = "APIs для мобильного приложения")
    @GetMapping("/{id}/plans/{planId}/file")
    @Operation(summary = "Получить чертеж")
    public ResponseEntity<Resource> getPlanFile(
            @PathVariable @Min(1) Long id, @PathVariable @Min(1) Long planId, Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        return utils.getResponseEntityFromFile("plan" , inspectionService.getPlanFile(userId, id, planId));
    }

    // ------------------------------------------------- Фотографии -------------------------------------------------

    @Tag(name = "Мобильное приложение", description = "APIs для мобильного приложения")
    @PostMapping( "/{id}/plans/{planId}/photos")
    @Operation(summary = "Загрузить фотографию на чертеж")
    public ResponseEntity<PhotoCreateDto> savePhoto(
            @PathVariable @Min(1) Long id, @PathVariable @Min(1) Long planId,
            @RequestParam("file") MultipartFile multipartFile, @RequestParam("data") @Valid PhotoCreateDto photoCreateDto,
            Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        return ResponseEntity.ok(
                photoMapper.mapToPhotoCreateDto(
                        inspectionService.updateOrCreatePhoto(userId, null, planId, photoCreateDto, multipartFile))
        );
    }
    @Tag(name = "Мобильное приложение", description = "APIs для мобильного приложения")
    @PutMapping("/{id}/plans/{planId}/photos/{photoId}")
    @Operation(summary = "Обновить фотографию на чертеже", description = "Если не задавать file, то изображение остается прежним")
    public ResponseEntity<PhotoCreateDto> updatePhoto(
            @PathVariable @Min(1) Long id, @PathVariable @Min(1) Long planId, @PathVariable @Min(1) Long photoId,
            @RequestParam("file") MultipartFile multipartFile, @RequestParam("data") @Valid PhotoCreateDto photoCreateDto,
            Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        return ResponseEntity.ok(
                photoMapper.mapToPhotoCreateDto(inspectionService.updateOrCreatePhoto(userId, photoId, planId, photoCreateDto, multipartFile))
        );
    }
    @Tag(name = "Мобильное приложение", description = "APIs для мобильного приложения")
    @GetMapping("/{id}/plans/{planId}/photos/{photoId}")
    @Operation(summary = "Получить фотографию из чертежа")
    public ResponseEntity<Resource> getPhoto(
            @PathVariable @Min(1) Long id, @PathVariable @Min(1) Long planId, @PathVariable @Min(1) Long photoId,
            Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        return utils.getResponseEntityFromFile("photo", inspectionService.getPhotoFromPlan(userId, id, planId, photoId));
    }
    @Tag(name = "Мобильное приложение", description = "APIs для мобильного приложения")
    @DeleteMapping("/{id}/plans/{planId}/photos/{photoId}")
    @Operation(summary = "Удалить фотографию из чертежа")
    public ResponseEntity<Resource> deletePhoto(
            @PathVariable @Min(1) Long id, @PathVariable @Min(1) Long planId, @PathVariable @Min(1) Long photoId,
            Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        inspectionService.deletePhotoFromPlan(userId, photoId);
        return ResponseEntity.ok().build();
    }

    @Tag(name = "Мобильное приложение", description = "APIs для мобильного приложения")
    @PostMapping("/{id}/plans/{planId}/photos/{photoId}/category/{categoryId}")
    @Operation(summary = "Добавить фото из чертежа в категорию")
    public ResponseEntity<IdentifiableDto> movePhotoFromPlanToCategory(
            @PathVariable @Min(1) Long id, @PathVariable @Min(1) Long planId, @PathVariable @Min(1) Long photoId,
            @PathVariable @Min(1) Long categoryId,
            Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);

        return ResponseEntity.ok(
                commonMapper.mapToIdentifiableDto(
                        inspectionService.copyPhotoToCategoryFromPlan(userId, id, planId, photoId, categoryId))
        );
    }



    @Tag(name = "Мобильное приложение", description = "APIs для мобильного приложения")
    @PostMapping("/{id}/type-defect")
    @Operation(summary = "Создать тип дефекта к инспекции")
    public ResponseEntity<IdentUuid> createTypeDefect(
            @PathVariable @Min(1) Long id,  @RequestBody TypeDefectDto typeDefectDto,
            Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        //// TODO вызов создания
        return ResponseEntity.ok(new IdentUuid());
    }

    @Tag(name = "Мобильное приложение", description = "APIs для мобильного приложения")
    @GetMapping("/{id}/type-defect")
    @Operation(summary = "Все типы дефектов инспекции")
    public ResponseEntity<InspectionWithTypeDefect> getAllTypeDefects(
            @PathVariable @Min(1) Long id, Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        //// TODO вызов получения
        return ResponseEntity.ok(new InspectionWithTypeDefect());
    }

    @Tag(name = "Мобильное приложение", description = "APIs для мобильного приложения")
    @GetMapping("/{id}/plans/{planId}")
    @Operation(summary = "Получить все дефекты на плане")
    public ResponseEntity<PlanWithDefects> getAllPlanDefect(
            @PathVariable @Min(1) Long id,  @PathVariable @Min(1) Long planId, Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        //// TODO вызов создания
        return ResponseEntity.ok(new PlanWithDefects());
    }

    @Tag(name = "Мобильное приложение", description = "APIs для мобильного приложения")
    @PostMapping("/{id}/plans/{planId}")
    @Operation(summary = "Добавить дефект на план")
    public ResponseEntity<IdentUuid> createPlanDefect(
            @PathVariable @Min(1) Long id,  @PathVariable @Min(1) Long planId, @RequestBody PlanDefectDto planDefectDto,
            Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        //// TODO вызов создания
        return ResponseEntity.ok(new IdentUuid());
    }

    @Tag(name = "Мобильное приложение", description = "APIs для мобильного приложения")
    @DeleteMapping("/{id}/plans/{planId}/defects/{defectId}")
    @Operation(summary = "Удалить дефект с плана")
    public ResponseEntity<Void> deletePlanDefect(
            @PathVariable @Min(1) Long id,  @PathVariable @Min(1) Long planId, @PathVariable @Min(1) Long defectId,
            Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        //// TODO вызов создания
        return ResponseEntity.ok().build();
    }


    // ------------------------------------------------- Инспекция -------------------------------------------------

    @GetMapping("/{id}")
    @Operation(summary = "Получение информации об инспекции")
    public ResponseEntity<GetInspectionDto> getInspectionInfo(
            @PathVariable @Min(1) Long id, Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        return ResponseEntity
                .ok(inspectionMapper.mapToGetInspectionDto(inspectionService.getUserInspection(userId, id)));
    }

    @PostMapping("/{id}/docx")
    @Operation(summary = "Добавить отчет в очередь генерации")
    public ResponseEntity<Error> getCategoryPhoto(@PathVariable @Min(1) Long id, Authentication authentication
    ) {
        Long userId = utils.getUserId(authentication);
        boolean sendToGenerate = inspectionService.addTaskForCreatingDocument(id, userId);
        if (sendToGenerate) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body(new Error("Inspection already in analyze"));
    }

    @GetMapping("/{id}/docx")
    @Operation(summary = "Скачать файла", description = "Перед этим требуется проверить, доступен ли файл для скачивания")
    public ResponseEntity<Resource> getInspectionReport(@PathVariable @Min(1) Long id, Authentication authentication) {
        Long userId = utils.getUserId(authentication);
        StorageService.BytesWithContentType file = inspectionService.getUserInspectionReport(id, userId);
        String fileName =
                Joiner.on('_').join(
                        Optional.ofNullable(file.getName()).orElse("Отчет").replace(' ', '_'),
                        LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                );
        return ResponseEntity.ok().header(
                        "Content-Disposition",
                        "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + ".docx")
                .contentType(new MediaType("application", "vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(new ByteArrayResource(file.getBytes()));
    }

    @GetMapping("/{id}/docx/status")
    @Operation(summary = "Проверка статуса отчета")
    public ResponseEntity<DocumentStatusDto> getInspectionReportStatus(@PathVariable @Min(1) Long id, Authentication authentication) {
        Long userId = utils.getUserId(authentication);
        return ResponseEntity.ok(
                new DocumentStatusDto(inspectionService.getReportStatus(id, userId))
        );
    }
}
