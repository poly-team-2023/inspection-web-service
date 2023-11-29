package com.service.inspection.service;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import com.deepoove.poi.XWPFTemplate;
import com.service.inspection.configs.BucketName;
import com.service.inspection.document.DocumentModel;
import com.service.inspection.dto.inspection.InspectionDto;
import com.service.inspection.entities.Category;
import com.service.inspection.entities.Identifiable;
import com.service.inspection.entities.Inspection;
import com.service.inspection.entities.Photo;
import com.service.inspection.entities.User;
import com.service.inspection.entities.enums.ProgressingStatus;
import com.service.inspection.mapper.CategoryMapper;
import com.service.inspection.mapper.InspectionMapper;
import com.service.inspection.mapper.PhotoMapper;
import com.service.inspection.mapper.document.DocumentMapper;
import com.service.inspection.repositories.CategoryRepository;
import com.service.inspection.repositories.InspectionRepository;
import com.service.inspection.repositories.PhotoRepository;
import com.service.inspection.repositories.UserRepository;
import com.service.inspection.utils.ServiceUtils;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


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
    private final DocumentModelService documentModelService;
    private final File mainTemplate;

    @Transactional
    public Identifiable createInspection(Long userId) {
        User user = serviceUtils.tryToFindByID(userRepository, userId); // TODO

        Inspection inspection = new Inspection();
        inspection.setUsers(new HashSet<>(Set.of(user)));
        inspection.setName("Без названия"); // TODO нормально реализовать через проперти
        user.addInspection(inspection);

        inspectionRepository.save(inspection);
        return inspection;
    }

    public Page<Inspection> getUserInspections(Long userId, Integer pageSize, Integer pageNum) {
        return inspectionRepository.findByUsersId(userId, PageRequest.of(pageNum, pageSize));
    }

    public Inspection getUserInspection(Long userId, Long inspectionId) {
        return getInspectionIfExistForUser(inspectionId, userId);
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
    public void createDocument(Long inspectionId, Long userId) {
        Inspection inspection = getInspectionIfExistForUser(inspectionId, userId);
        moveInspectionStatus(inspection, ProgressingStatus.WAIT_ANALYZE);

        DocumentModel documentModel = documentMapper.mapToDocumentModel(inspection);

        List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
        Set<Category> categories = inspection.getCategories();
        for (Category category: categories) {
            Hibernate.initialize(category.getPhotos());

            completableFutures.add(
                documentModelService.processAllPhotosAsync(category.getPhotos()).thenAccept(x -> {
                    log.info("Добавление категории к модели" + category.getName());
                    documentModel.addCategory(documentMapper.mapToCategoryModel(category, x));
                    log.info("Окончание добавления категории к модели" + category.getName());
                })
            );

        }

        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).thenAccept(x -> {

            try (
                    XWPFTemplate template = XWPFTemplate.compile(mainTemplate).render(documentModel);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ) {
                    template.write(byteArrayOutputStream);
                    InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

                    saveDocxFileFile(inspection, inputStream);
                } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }


    private Inspection getInspectionIfExistForUser(Long inspectionId, Long userId) {
        Inspection inspection = inspectionRepository.findByUsersIdAndId(userId, inspectionId);
        if (inspection == null) {
            throw new EntityNotFoundException(String.format("No such inspection with id %s for this user", inspectionId));
        }
        return inspection;
    }


    private void saveDocxFileFile(Inspection inspection, InputStream inputStream) {
        UUID uuid = UUID.randomUUID();

        inspection.setStatus(ProgressingStatus.READY);
        inspection.setReportUuid(uuid);
        inspectionRepository.save(inspection);

        storageService.saveFile(BucketName.DOCUMENT, uuid.toString(), inputStream);
    }

    public void moveInspectionStatus(Inspection inspection, ProgressingStatus targetStatus) {
        ProgressingStatus status = inspection.getStatus();
//        if (status == targetStatus) {
//            throw new RuntimeException();
//        }
        inspection.setStatus(targetStatus);
        inspectionRepository.save(inspection);
    }
}
