package com.service.inspection.service;

import com.service.inspection.configs.BucketName;
import com.service.inspection.dto.equipment.EquipmentDto;
import com.service.inspection.entities.Equipment;
import com.service.inspection.entities.User;
import com.service.inspection.mapper.EquipmentMapper;
import com.service.inspection.repositories.EquipmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EquipmentService {

    EquipmentRepository equipmentRepository;
    EquipmentMapper equipmentMapper;
    StorageService storageService;

    public Equipment get(long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Equipment with id " + id + " not found"));
    }

    public List<Equipment> getEquipment(User user) {
        return (List<Equipment>) equipmentRepository.findEquipmentByUserId(user.getId());
    }

    public void addEquipment(User user, Equipment equipment) {
        equipment.setUser(user);
        equipmentRepository.save(equipment);
    }

    public void updateEquipment(User user, long id, EquipmentDto dto) {
        Equipment equipment = get(id);
        checkUser(equipment, user);

        equipmentMapper.mapToUpdateEquipment(equipment, dto);
        equipmentRepository.save(equipment);
    }

    public void deleteEquipment(User user, long id) {
        checkUser(get(id), user);
        equipmentRepository.deleteById(id);
        // TODO : deletePicture()
    }

    @Transactional
    public void addPicture(User user, long id, MultipartFile picture) {
        Equipment equipment = get(id);
        checkUser(equipment, user);

        UUID pictureUuid = UUID.randomUUID();
        storageService.saveFile(BucketName.VERIFICATION_SCAN, pictureUuid.toString(), picture);
        equipment.setVerificationScanUuid(pictureUuid);
        equipmentRepository.save(equipment);
    }

    private void checkUser(Equipment equipment, User user) {
        if (!equipment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No access");
        }
    }
}
