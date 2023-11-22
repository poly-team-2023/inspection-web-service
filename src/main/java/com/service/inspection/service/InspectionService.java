package com.service.inspection.service;

import com.deepoove.poi.XWPFTemplate;
import com.service.inspection.configs.BucketName;
import com.service.inspection.dto.inspection.InspectionDto;
import com.service.inspection.entities.*;
import com.service.inspection.mapper.CategoryMapper;
import com.service.inspection.mapper.InspectionMapper;
import com.service.inspection.mapper.PhotoMapper;
import com.service.inspection.mapper.document.DocumentMapper;
import com.service.inspection.repositories.CategoryRepository;
import com.service.inspection.repositories.InspectionRepository;
import com.service.inspection.repositories.PhotoRepository;
import com.service.inspection.repositories.UserRepository;
import com.service.inspection.service.document.ImageProcessingFacade;
import com.service.inspection.service.document.TestImageModel;
import com.service.inspection.utils.ServiceUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class InspectionService {

    private final InspectionRepository inspectionRepository;
    private final UserRepository userRepository;
    private final InspectionMapper inspectionMapper;
    private final StorageService storageService;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final PhotoMapper photoMapper;
    private final ServiceUtils serviceUtils;
    private final PhotoRepository photoRepository;
    private final DocumentMapper documentMapper;
    private final ImageProcessingFacade imageProcessingFacade;
    private final DocumentModelService documentModelService;

    @Transactional
    public Long createInspection(Long userId) {
        User user = serviceUtils.tryToFindByID(userRepository, userId); // TODO

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

    public void deleteInspection(Long inspectionId, Long userId) {
        Inspection inspection = getInspectionIfExistForUser(inspectionId, userId);
        inspectionRepository.deleteById(inspection.getId());
        // TODO проверка на наличие именно этой инспекци у пользователя + обработка ошибок
    }

    public void updateInspection(Long inspectionId, Long userId, InspectionDto inspectionDto) {
        Inspection inspection = getInspectionIfExistForUser(inspectionId, userId);
        inspectionMapper.mapToInspection(inspection, inspectionDto);
        inspectionRepository.save(inspection);

        // TODO проверка на наличие companyId и employerId в
        //  базе, потому что сейчас при данной проблеме мы будем
        //  получать o.h.engine.jdbc.spi.SqlExceptionHelper
    }

    @Transactional
    public void uploadMainInspectionPhoto(Long userId, Long inspectionId, MultipartFile multipartFile) {
        UUID uuid = UUID.randomUUID();

        Inspection inspection = getInspectionIfExistForUser(inspectionId, userId);
        inspection.setMainPhotoName(multipartFile.getOriginalFilename());
        inspection.setMainPhotoUuid(uuid);

        inspectionRepository.save(inspection);
        storageService.saveFile(BucketName.INSPECTION_MAIN_PHOTO, uuid.toString(), multipartFile);
    }

    public StorageService.BytesWithContentType getMainInspectionPhoto(Long inspectionId, Long userId) {
        Inspection inspection = getInspectionIfExistForUser(inspectionId, userId);
        if (inspection.getMainPhotoUuid() == null) {
            return null;
        }
        return storageService.getFile(BucketName.INSPECTION_MAIN_PHOTO, inspection.getMainPhotoUuid().toString());
    }

    // --------------------------------------- category -----------------------------------------

    public Identifiable createNewCategory(Long userId, Long inspectionId, String categoryName) {
        Inspection inspection = getInspectionIfExistForUser(inspectionId, userId);
        Category category = categoryMapper.mapToCategory(categoryName, inspection);

        inspection.addCategory(category);
        inspectionRepository.save(inspection);
        return category;
    }

    public Set<Category> getAllCategories(Long inspectionId, Long userId) {
        Inspection inspection = getInspectionIfExistForUser(inspectionId, userId);
        return Optional.ofNullable(inspection)
                .map(Inspection::getCategories)
                .orElse(null);
    }

    public void updateCategory(Long userId, Long categoryId, Long inspectionId, String newName) {
        Inspection inspection = getInspectionIfExistForUser(inspectionId, userId);
        Category category = serviceUtils.tryToFindByID(inspection.getCategories(), categoryId);

        categoryMapper.mapToCategory(category, newName);
        categoryRepository.save(category);
    }

    public void deleteCategory(Long userId, Long inspectionId, Long categoryId) {
        Inspection inspection = getInspectionIfExistForUser(inspectionId, userId);
        Category category = serviceUtils.tryToFindByID(inspection.getCategories(), categoryId);

        inspection.getCategories().remove(category);
        categoryRepository.save(category);
    }

    // --------------------------------------- photo -----------------------------------------

    @Transactional
    public Identifiable addPhotoToCategory(Long userId, Long inspectionId, Long categoryId, MultipartFile multipartFile) {
        Inspection inspection = getInspectionIfExistForUser(inspectionId, userId);
        Category category = serviceUtils.tryToFindByID(inspection.getCategories(), categoryId);

        // TODO прикрепление к плану
        UUID uuid = UUID.randomUUID();
        Photo photo = photoMapper.mapToPhoto(multipartFile.getOriginalFilename(), uuid, category, null);
        category.addPhoto(photo);

        categoryRepository.save(category);
        storageService.saveFile(BucketName.CATEGORY_PHOTOS, uuid.toString(), multipartFile);
        return photo;
    }

    public void deletePhoto(Long userId, Long inspectionId, Long categoryId, Long photoId) {
        Inspection inspection = getInspectionIfExistForUser(inspectionId, userId);
        Category category = serviceUtils.tryToFindByID(inspection.getCategories(), categoryId);
        Photo photo = serviceUtils.tryToFindByID(category.getPhotos(), photoId);   // TODO анализ производительности

        category.getPhotos().remove(photo);
        photoRepository.deleteById(photo.getId());
    }

    public StorageService.BytesWithContentType getCategoryPhoto(Long userId, Long inspectionId, Long categoryId, Long photoId) {
        Inspection inspection = getInspectionIfExistForUser(inspectionId, userId); // TODO анализ производительности
        Category category = serviceUtils.tryToFindByID(inspection.getCategories(), categoryId);
        Photo photo = serviceUtils.tryToFindByID(category.getPhotos(), photoId);
        if (photo.getFileUuid() == null) {
            return null;
        }
        return storageService.getFile(BucketName.CATEGORY_PHOTOS, photo.getFileUuid().toString());
    }

    // --------------------------------------- create-document -----------------------------------------
    public void createDocument(Long inspectionId) {

        File file;
        try {
            file = ResourceUtils.getFile("classpath:test-template2.docx");
        } catch (FileNotFoundException f) {
            return;
        }

        Set<Photo> photos = new HashSet<>();
        Inspection inspection = inspectionRepository.findById(inspectionId).orElse(null);
        Set<Category> categories = inspection.getCategories();
        for (Category category: categories) {
            photos.addAll(category.getPhotos());
        }

        documentModelService.processAllPhotosAsync(photos);
        log.info(Thread.currentThread().getName());


//        try (XWPFTemplate template = XWPFTemplate.compile(file).render(
//                documentMapper.mapToDocumentModel(inspectionRepository.findById(inspectionId).orElse(null)))) {
//            template.writeToFile("compile-result.docx");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }


    private Inspection getInspectionIfExistForUser(Long inspectionId, Long userId) {
        Inspection inspection = inspectionRepository.findByUsersIdAndId(userId, inspectionId);
        if (inspection == null) {
            throw new EntityNotFoundException(String.format("No such inspection with id %s for this user", inspectionId));
        }
        return inspection;
    }
}
