package com.service.inspection.service;

import com.service.inspection.configs.BucketName;
import com.service.inspection.dto.inspection.InspectionDto;
import com.service.inspection.entities.Category;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.Identifiable;
import com.service.inspection.entities.Inspection;
import com.service.inspection.entities.Named;
import com.service.inspection.entities.User;
import com.service.inspection.mapper.CategoryMapper;
import com.service.inspection.mapper.InspectionMapper;
import com.service.inspection.repositories.CategoryRepository;
import com.service.inspection.repositories.CompanyRepository;
import com.service.inspection.repositories.InspectionRepository;
import com.service.inspection.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InspectionService {

    private final InspectionRepository inspectionRepository;
    private final UserRepository userRepository;
    private final InspectionMapper inspectionMapper;
    private final StorageService storageService;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public Long createInspection(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(RuntimeException::new); // TODO

        Inspection inspection = new Inspection();
        inspection.setUsers(new HashSet<>(Set.of(user)));
        inspection.setName("Без названия"); // TODO нормально реализовать через проперти
        user.addInspection(inspection);

        inspectionRepository.save(inspection);
        return inspection.getId();
    }

    public Page<Inspection> getUserInspection(Long userId, Integer pageSize, Integer pageNum) {
        return inspectionRepository.findByUsersId(userId, PageRequest.of(pageNum, pageSize));
    }

    public void deleteInspection(Long inspectionId) {
        inspectionRepository.deleteById(inspectionId);
        // TODO проверка на наличие именно этой инспекци у пользователя + обработка ошибок
    }

    public void updateInspection(Long inspectionId, Long userId, InspectionDto inspectionDto) {
        Inspection inspection =
                Optional.ofNullable(inspectionRepository.findByUsersIdAndId(userId, inspectionId))
                        .orElseThrow(RuntimeException::new);
        inspectionMapper.mapToInspection(inspection, inspectionDto);
        inspectionRepository.save(inspection);

        // TODO проверка на наличие companyId и employerId в
        //  базе, потому что сейчас при данной проблеме мы будем
        //  получать o.h.engine.jdbc.spi.SqlExceptionHelper
    }

    @Transactional
    public void uploadMainInspectionPhoto(Long userId, Long inspectionId, MultipartFile multipartFile) {
        UUID uuid = UUID.randomUUID();

        Inspection inspection = inspectionRepository.findByUsersIdAndId(userId, inspectionId);
        inspection.setMainPhotoName(multipartFile.getOriginalFilename());
        inspection.setMainPhotoUuid(uuid);

        inspectionRepository.save(inspection);
        storageService.saveFile(BucketName.INSPECTION_MAIN_PHOTO, uuid.toString(), multipartFile);
    }

    public StorageService.BytesWithContentType getMainInspectionPhoto(Long inspectionId) {
        Inspection inspection = inspectionRepository.findById(inspectionId).orElse(null);
        if (inspection == null) return null;
        return storageService.getFile(BucketName.INSPECTION_MAIN_PHOTO, inspection.getMainPhotoUuid().toString());
    }


    public Identifiable createNewCategory(Long userId, Long inspectionId, String categoryName) {
        Inspection inspection = inspectionRepository.findByUsersIdAndId(userId, inspectionId); // TODO
        Category category = categoryMapper.mapToCategory(categoryName, inspection);
        inspection.addCategory(category);
        return inspectionRepository.save(inspection);
    }

//    public Set<Named> getAllCategories(Long inspectionId, Long userId) {
//        Inspection inspection = inspectionRepository.findByUsersIdAndId(userId, inspectionId);
//        return Optional.ofNullable(inspection)
//                .map(Inspection::getCategories)
//                .orElse(null);
//    }
}
