package com.service.inspection.controller;

import java.util.List;
import com.service.inspection.dto.IdentifiableDto;
import com.service.inspection.dto.equipment.EquipmentDto;
import com.service.inspection.dto.equipment.GetEquipmentDto;
import com.service.inspection.entities.User;
import com.service.inspection.mapper.CommonMapper;
import com.service.inspection.mapper.EquipmentMapper;
import com.service.inspection.service.EquipmentService;
import com.service.inspection.service.security.UserDetailsImpl;
import com.service.inspection.utils.ControllerUtils;

import jakarta.validation.constraints.Min;
import org.springframework.core.io.Resource;
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

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/equipment")
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
@AllArgsConstructor
public class EquipmentController {

    private static final String VERIFICATION_SCAN = "verification-scan";

    private final EquipmentService equipmentService;
    private final EquipmentMapper equipmentMapper;
    private final ControllerUtils controllerUtils;
    private final CommonMapper commonMapper;

    @GetMapping
    @Operation(summary = "Получить список оборудования")
    public ResponseEntity<List<GetEquipmentDto>> getEquipment(Authentication authentication) {
        return ResponseEntity.ok(equipmentService.getEquipment(controllerUtils.getUserId(authentication))
                .stream().map(equipmentMapper::mapToDto).toList());
    }

    @PostMapping
    @Operation(summary = "Добавить оборудование")
    public ResponseEntity<IdentifiableDto> addEquipment(@RequestBody @Valid EquipmentDto dto,
                                                        Authentication authentication) {
        User user = controllerUtils.getUser(authentication);
        return ResponseEntity.ok(commonMapper.mapToIdentifiableDto(
                equipmentService.addEquipment(user, equipmentMapper.mapToEquipment(dto))));
    }

    @PutMapping("/{equip_id}")
    @Operation(summary = "Обновить сведения об оборудовании")
    public ResponseEntity<Void> updateEquipment(@PathVariable("equip_id") @Min(1) long id,
                                                @RequestBody @Valid EquipmentDto dto,
                                                Authentication authentication) {
        equipmentService.updateEquipment(controllerUtils.getUserId(authentication), id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{equip_id}")
    @Operation(summary = "Удаление оборудования")
    public ResponseEntity<Void> deleteEquipment(@PathVariable("equip_id") @Min(1) long id,
                                                Authentication authentication) {
        equipmentService.deleteEquipment(controllerUtils.getUserId(authentication), id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{equip_id}/file")
    @Operation(summary = "Добавить скан к оборудованию")
    public ResponseEntity<IdentifiableDto> addFile(@PathVariable("equip_id") @Min(1) long id,
                                                   @RequestParam("scanNumber") int scanNumber,
                                                   MultipartFile file,
                                                   Authentication authentication) {
        return ResponseEntity.ok(commonMapper.mapToIdentifiableDto(equipmentService.addScan(
                controllerUtils.getUserId(authentication), id, scanNumber, file)));
    }

    @PutMapping("/{equip_id}/file/{file_id}")
    @Operation(summary = "Обновить скан")
    public ResponseEntity<Void> updateFile(@PathVariable("equip_id") @Min(1) long equipId,
                                           @PathVariable("file_id") @Min(1) long fileId,
                                           @RequestParam("scanNumber") int scanNumber,
                                           Authentication authentication) {
        equipmentService.updateScan(controllerUtils.getUserId(authentication), equipId, fileId, scanNumber);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{equip_id}/file/{file_id}")
    @Operation(summary = "Удалить скан")
    public ResponseEntity<Void> deleteFile(@PathVariable("equip_id") @Min(1) long equipId,
                                           @PathVariable("file_id") @Min(1) long fileId,
                                           Authentication authentication) {
        equipmentService.deleteScan(controllerUtils.getUserId(authentication), equipId, fileId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{equip_id}/file")
    @Operation(summary = "Удалить все сканы")
    public ResponseEntity<Void> deleteAllFiles(@PathVariable("equip_id") @Min(1) long equipId,
                                               Authentication authentication) {
        equipmentService.deleteAllScan(controllerUtils.getUserId(authentication), equipId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{equip_id}/file/{file_id}")
    public ResponseEntity<Resource> getFile(@PathVariable("equip_id") @Min(1) long equipId,
                                            @PathVariable("file_id") @Min(1) long fileId,
                                            Authentication authentication) {
        return controllerUtils.getResponseEntityFromFile(
                VERIFICATION_SCAN,
                equipmentService.getScan(controllerUtils.getUserId(authentication), equipId, fileId)
        );
    }
}
