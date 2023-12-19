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

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/equipment")
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
@AllArgsConstructor
public class EquipmentController {

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
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        return ResponseEntity.ok(commonMapper.mapToIdentifiableDto(
                equipmentService.addEquipment(user, equipmentMapper.mapToEquipment(dto))));
    }

    @PutMapping("/{equip_id}")
    @Operation(summary = "Обновить сведения об оборудовании")
    public ResponseEntity<Void> updateEquipment(@PathVariable("equip_id") long id,
                                                @RequestBody @Valid EquipmentDto dto,
                                                Authentication authentication) {
        equipmentService.updateEquipment(controllerUtils.getUserId(authentication), id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{equip_id}")
    @Operation(summary = "Удаление оборудования")
    public ResponseEntity<Void> deleteEquipment(@PathVariable("equip_id") long id,
                                                Authentication authentication) {
        equipmentService.deleteEquipment(controllerUtils.getUserId(authentication), id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{equip_id}/file")
    @Operation(summary = "Добавить скан к оборудованию")
    public ResponseEntity<IdentifiableDto> addFile(@PathVariable("equip_id") long id,
                                                   @RequestParam("scanNumber") int scanNumber,
                                                   MultipartFile file,
                                                   Authentication authentication) {
        return ResponseEntity.ok(commonMapper.mapToIdentifiableDto(equipmentService.addScan(
                controllerUtils.getUserId(authentication), id, scanNumber, file)));
    }

    @PutMapping("/{equip_id}/file/{file_id}")
    @Operation(summary = "Обновить скан")
    public ResponseEntity<Void> updateFile(@PathVariable("equip_id") long equipId,
                                           @PathVariable("file_id") long fileId,
                                           @RequestParam("scanNumber") int scanNumber,
                                           Authentication authentication) {
        equipmentService.updateScan(controllerUtils.getUserId(authentication), equipId, fileId, scanNumber);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{equip_id}/file/{file_id}")
    @Operation(summary = "Удалить скан")
    public ResponseEntity<Void> deleteFile(@PathVariable("equip_id") long equipId,
                                           @PathVariable("file_id") long fileId,
                                           Authentication authentication) {
        equipmentService.deleteScan(controllerUtils.getUserId(authentication), equipId, fileId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{equip_id}/file")
    @Operation(summary = "Удалить все сканы")
    public ResponseEntity<Void> deleteAllFiles(@PathVariable("equip_id") long equipId,
                                               Authentication authentication) {
        equipmentService.deleteAllScan(controllerUtils.getUserId(authentication), equipId);
        return ResponseEntity.ok().build();
    }
}
