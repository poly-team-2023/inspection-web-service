package com.service.inspection.service;

import java.util.List;
import java.util.UUID;

import com.service.inspection.configs.BucketName;
import com.service.inspection.dto.equipment.EquipmentDto;
import com.service.inspection.entities.Equipment;
import com.service.inspection.entities.FileScan;
import com.service.inspection.entities.Identifiable;
import com.service.inspection.entities.User;
import com.service.inspection.mapper.EquipmentMapper;
import com.service.inspection.repositories.EquipmentRepository;

import com.service.inspection.repositories.FileScanRepository;
import com.service.inspection.utils.ServiceUtils;
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
    private final FileScanRepository fileScanRepository;
    private final ServiceUtils serviceUtils;


    public List<Equipment> getEquipment(long userId) {
        return equipmentRepository.findEquipmentByUserId(userId);
    }

    public Identifiable addEquipment(User user, Equipment equipment) {
        equipment.setUser(user);
        equipmentRepository.save(equipment);
        return equipment;
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
    public Identifiable addScan(long userId, long equipmentId, int scanNumber, MultipartFile file) {
        Equipment equipment = getEquipmentIfExistForUser(equipmentId, userId);
        UUID pictureUuid = UUID.randomUUID();

        FileScan fileScan = new FileScan();
        fileScan.setEquipment(equipment);
        fileScan.setName(file.getOriginalFilename());
        fileScan.setFileUuid(pictureUuid);
        fileScan.setScanNumber(scanNumber);
        fileScanRepository.save(fileScan);

        storageService.saveFile(BucketName.VERIFICATION_SCAN, pictureUuid.toString(), file);
        return fileScan;
    }

    public void updateScan(long userId, long equipmentId, long scanId, int scanNumber) {
        FileScan scan = serviceUtils.tryToFindByID(
                getEquipmentIfExistForUser(equipmentId, userId).getFiles(), scanId);
        scan.setScanNumber(scanNumber);
        fileScanRepository.save(scan);
    }

    public void deleteScan(long userId, long equipmentId, long scanId) {
        serviceUtils.tryToFindByID(
                getEquipmentIfExistForUser(equipmentId, userId).getFiles(), scanId);
        fileScanRepository.deleteById(scanId);
    }

    public void deleteAllScan(long userId, long equipmentId) {
        Equipment equipment = getEquipmentIfExistForUser(equipmentId, userId);
        fileScanRepository.deleteAll(equipment.getFiles());
    }


    public Equipment getEquipmentIfExistForUser(Long equipmentId, Long userId) {
        return equipmentRepository.findByUserIdAndId(userId, equipmentId).orElseThrow(() ->
                new EntityNotFoundException(String.format("No such equipment with id %s for this user", equipmentId)));
    }

    public StorageService.BytesWithContentType getScan(Long userId, Long equipId, Long scanId) {
        FileScan scan = serviceUtils.tryToFindByID(getEquipmentIfExistForUser(equipId, userId).getFiles(), scanId);
        return storageService.getFile(BucketName.VERIFICATION_SCAN, scan.getFileUuid().toString());
    }
}
