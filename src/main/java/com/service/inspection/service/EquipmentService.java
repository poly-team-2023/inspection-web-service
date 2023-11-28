package com.service.inspection.service;

import java.util.List;
import java.util.UUID;

import com.service.inspection.configs.BucketName;
import com.service.inspection.dto.equipment.EquipmentDto;
import com.service.inspection.entities.Equipment;
import com.service.inspection.entities.User;
import com.service.inspection.mapper.EquipmentMapper;
import com.service.inspection.repositories.EquipmentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;
    private final StorageService storageService;


    public List<Equipment> getEquipment(long userId) {
        return (List<Equipment>) equipmentRepository.findEquipmentByUserId(userId);
    }

    public void addEquipment(User user, Equipment equipment) {
        equipment.setUser(user);
        equipmentRepository.save(equipment);
    }

    public void updateEquipment(long userId, long equipmentId, EquipmentDto dto) {
        Equipment equipment = getEquipmentIfExistForUser(equipmentId, userId);

        equipmentMapper.mapToUpdateEquipment(equipment, dto);
        equipmentRepository.save(equipment);
    }

    public void deleteEquipment(long userId, long equipmentId) {
        getEquipmentIfExistForUser(equipmentId, userId);
        equipmentRepository.deleteById(equipmentId);
        // TODO : deletePicture()
    }

    @Transactional
    public void addPicture(long userId, long equipmentId, MultipartFile picture) {
        Equipment equipment = getEquipmentIfExistForUser(equipmentId, userId);
        UUID pictureUuid = UUID.randomUUID();

        equipment.setVerificationScanUuid(pictureUuid);
        equipment.setVerificationScanName(picture.getOriginalFilename());
        equipmentRepository.save(equipment);

        storageService.saveFile(BucketName.VERIFICATION_SCAN, pictureUuid.toString(), picture);
    }

    public Equipment getEquipmentIfExistForUser(Long equipmentId, Long userId) {
        return equipmentRepository.findByUserIdAndId(userId, equipmentId).orElseThrow(() ->
                new EntityNotFoundException(String.format("No such equipment with id %s for this user", equipmentId)));
    }
}
