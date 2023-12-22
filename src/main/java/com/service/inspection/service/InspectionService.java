package com.service.inspection.service;

import com.deepoove.poi.XWPFTemplate;
import com.google.common.base.Stopwatch;
import com.service.inspection.advice.MessageException;
import com.service.inspection.configs.BucketName;
import com.service.inspection.document.DocumentModel;
import com.service.inspection.document.model.CategoryModel;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
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
    @Qualifier("mainTemplatePath")
    private final String templatePath;
    private final ResourceLoader resourceLoader;


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
        // TODO проверка на наличие именно этой инспекции у пользователя + обработка ошибок
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

    public List<Category> getAllCategories(Long inspectionId, Long userId) {
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
    @Transactional
    public void createDocument(Long inspectionId, Long userId) {
        Stopwatch timer = Stopwatch.createStarted();

        Inspection inspection = getInspectionIfExistForUser(inspectionId, userId);
        User user = userRepository.findById(userId).orElse(null);

        if (inspection.getStatus() == ProgressingStatus.WAIT_ANALYZE) {
            throw new MessageException(HttpStatus.TOO_EARLY, "Inspection already in analyze");
        }

        inspection.setStatus(ProgressingStatus.WAIT_ANALYZE);
        inspectionRepository.save(inspection);

        log.info("Start creating inspection document for inspection {}", inspectionId);
        List<CompletableFuture<Void>> futureResult = new ArrayList<>();
        DocumentModel documentModel = documentMapper.mapToDocumentModel(inspection, user, futureResult);
        CompletableFuture.allOf(futureResult.toArray(new CompletableFuture[0])).thenAccept(x -> {
            if (documentModel.getCategories() != null) {
                documentModel.getCategories().sort(Comparator.comparingLong(CategoryModel::getCategoryNum));
            }
            log.info("Start filling template {}", inspectionId);
            try (
                    XWPFTemplate template = XWPFTemplate
                            .compile(resourceLoader.getResource(templatePath).getInputStream())
                            .render(documentModel);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
            ) {

                template.write(byteArrayOutputStream);
                InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                UUID fileUuid = saveDocxFileFile(inspection, inputStream);
                log.info("Saved file uuid {} for inspection {}. Takes: {}", fileUuid, inspectionId, timer.stop());
            } catch (IOException e) {
               log.error(e.getMessage());
            }
        });
    }

    public ProgressingStatus getReportStatus(Long inspectionId, Long userId) {
        // TODO метод для загрузки только одного поля
        Inspection inspection = getInspectionIfExistForUser(inspectionId, userId);
        return inspection.getStatus();
    }

    public StorageService.BytesWithContentType getUserInspectionReport(Long inspectionId, Long userId) {
        Inspection inspection = getInspectionIfExistForUser(inspectionId, userId);
        if (inspection.getStatus() != ProgressingStatus.READY) {
            throw new MessageException(HttpStatus.TOO_EARLY, "Report still not ready");
        }
        StorageService.BytesWithContentType file =
                storageService.getFile(BucketName.DOCUMENT, inspection.getReportUuid().toString());
        // TODO мега костыль стоит сделать чтобы при загрузке подгружалось пользовательское назнвание файла
        file.setName(inspection.getReportName());
        return file;
    }

    private Inspection getInspectionIfExistForUser(Long inspectionId, Long userId) {
        Inspection inspection = inspectionRepository.findByUsersIdAndId(userId, inspectionId);
        if (inspection == null) {
            throw new EntityNotFoundException(String.format("No such inspection with id %s for this user", inspectionId));
        }
        return inspection;
    }


    private UUID saveDocxFileFile(Inspection inspection, InputStream inputStream) {
        UUID uuid = UUID.randomUUID();

        Inspection inspection1 = inspectionRepository.findById(inspection.getId()).orElse(null);

        inspection1.setStatus(ProgressingStatus.READY);
        inspection1.setReportUuid(uuid);

        inspectionRepository.save(inspection1);

        storageService.saveFile(BucketName.DOCUMENT, uuid.toString(), inputStream);
        return uuid;
    }

}
