package com.service.inspection.controller;

import java.util.List;
import java.util.Set;

import com.service.inspection.dto.IdentifiableDto;
import com.service.inspection.dto.NamedDto;
import com.service.inspection.dto.inspection.InspectionDto;
import com.service.inspection.dto.inspection.InspectionWithIdOnly;
import com.service.inspection.dto.inspection.InspectionWithName;
import com.service.inspection.entities.Inspection;
import com.service.inspection.mapper.CommonMapper;
import com.service.inspection.mapper.InspectionMapper;
import com.service.inspection.service.InspectionService;
import com.service.inspection.service.StorageService;
import com.service.inspection.service.security.UserDetailsImpl;
import com.service.inspection.utils.ControllerUtils;

import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("/api/v1/inspections")
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;
    private final InspectionMapper inspectionMapper;
    private final ControllerUtils utils;
    private final CommonMapper commonMapper;


    // TODO заменить обращение к бд для поиска пользователя с email на
    //  id для более быстрого поиска

    @PostMapping
    @Operation(summary = "Создание пустой инспекции Без названия")
    public ResponseEntity<InspectionWithIdOnly> createInspection(Authentication authentication) {
        Long userId = utils.getUserId(authentication);
        Long inspectionId = inspectionService.createInspection(userId);

        InspectionWithIdOnly inspectionOnlyIdDto = new InspectionWithIdOnly();
        inspectionOnlyIdDto.setId(inspectionId);

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
        Page<Inspection> page = inspectionService.getUserInspection(id, pageSize, pageNum);

        return ResponseEntity.ok().body(
                page.map(inspectionMapper::mapToInspectionWithName)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление инспекции")
    public ResponseEntity<Void> deleteInspection(@PathVariable @Min(1) Long id) {
        inspectionService.deleteInspection(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление полей инспекции")
    public ResponseEntity<Void> updateInspection( @PathVariable @Min(1) Long id,
                                                  @RequestBody InspectionDto inspectionDto,
                                                  Authentication authentication
    ) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getUser().getId();
        inspectionService.updateInspection(id, userId, inspectionDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/main-photo")
    @Operation(summary = "Загрузка обложки\\главное фотографии проекта\\отчета")
    public ResponseEntity<Void> uploadMainPhoto(
            @PathVariable @Min(1) Long id,
            MultipartFile multipartFile, Authentication authentication) {
        Long userId = utils.getUserId(authentication);
        inspectionService.uploadMainInspectionPhoto(userId, id, multipartFile);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/main-photo")
    @Operation(summary = "Скачать главную фотографию отчета")
    public ResponseEntity<Resource> getInspectionMainPhoto(
            @PathVariable @Min(1) Long id
    ) {
        StorageService.BytesWithContentType file = inspectionService.getMainInspectionPhoto(id);
        return utils.getResponseEntityFromFile("main-photo", file);
    }

    // TODO загрузка загрузка файлов ()

    @PostMapping("/{id}/categories")
    public ResponseEntity<IdentifiableDto> createNewCategory(
            @PathVariable @Min(1) Long id,
            @RequestParam(name = "name") String categoryName,
            Authentication authentication

    ) {
        Long userId = utils.getUserId(authentication);
        return ResponseEntity.ok(
                commonMapper.mapToIdentifiableDto(inspectionService.createNewCategory(userId, id, categoryName))
        );
    }

//    @GetMapping("/{id}/categories")
//    public ResponseEntity<Set<NamedDto>> getAllCategories(
//            Authentication authentication, @PathVariable @Min(1) Long id
//    ) {
//        Long userId = utils.getUserId(authentication);
//    }
}
