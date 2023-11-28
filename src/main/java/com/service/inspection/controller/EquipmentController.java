package com.service.inspection.controller;

import java.util.List;

import com.service.inspection.dto.equipment.EquipmentDto;
import com.service.inspection.dto.equipment.GetEquipmentDto;
import com.service.inspection.entities.User;
import com.service.inspection.mapper.EquipmentMapper;
import com.service.inspection.service.EquipmentService;
import com.service.inspection.service.security.UserDetailsImpl;
import com.service.inspection.utils.ControllerUtils;

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

    private final EquipmentService equipmentService;
    private final EquipmentMapper equipmentMapper;
    private final ControllerUtils controllerUtils;

    @GetMapping
    @Operation(summary = "Получить список оборудования")
    public ResponseEntity<List<GetEquipmentDto>> getEquipment(Authentication authentication) {
        return ResponseEntity.ok(equipmentService.getEquipment(controllerUtils.getUserId(authentication))
                .stream().map(equipmentMapper::mapToDto).toList());
    }

    @PostMapping
    @Operation(summary = "Добавить оборудование")
    public ResponseEntity<Void> addEquipment(@RequestBody @Valid EquipmentDto dto,
                                             Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        equipmentService.addEquipment(user, equipmentMapper.mapToEquipment(dto));
        return ResponseEntity.ok().build();
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

    @PostMapping("/{equip_id}/pic")
    @Operation(summary = "Добавить скан к оборудованию")
    public ResponseEntity<Void> addPicture(@PathVariable("equip_id") long id,
                                           MultipartFile picture,
                                           Authentication authentication) {
        equipmentService.addPicture(controllerUtils.getUserId(authentication), id, picture);
        return ResponseEntity.ok().build();
    }
}
