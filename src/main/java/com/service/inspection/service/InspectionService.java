package com.service.inspection.service;

import com.service.inspection.advice.MessageException;
import com.service.inspection.configs.BucketName;
import com.service.inspection.dto.inspection.InspectionDto;
import com.service.inspection.dto.inspection.PhotoCreateDto;
import com.service.inspection.entities.*;
import com.service.inspection.entities.enums.ProgressingStatus;
import com.service.inspection.mapper.CategoryMapper;
import com.service.inspection.mapper.InspectionMapper;
import com.service.inspection.mapper.PhotoMapper;
import com.service.inspection.repositories.*;
import com.service.inspection.utils.ServiceUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
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
    private final DocumentService documentService;
    private final AnalyzeService analyzeService;
    private final InspectionFetcherEngine inspectionFetcherEngine;
    private final PlanRepository planRepository;
    private final PhotoPlanRepository photoPlanRepository;

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


    // --------------------------------------- plan -----------------------------------------

    @Transactional
    public Identifiable addPlanToInspection(Long userId, Long inspectionId, String name, MultipartFile multipartFile) {
        Inspection inspection = getUserInspection(userId, inspectionId);

        UUID uuid = UUID.randomUUID();

        Plan plan = new Plan();
        plan.setInspection(inspection);
        plan.setName(name);
        plan.setFileUuid(uuid);

        planRepository.save(plan);
        storageService.saveFile(BucketName.PlAN, uuid.toString(), multipartFile);

        return plan;
    }

    @Transactional
    public void deletePlanFromInspection(Long userId, Long inspectionId, Long planId) {
        Inspection inspection = getUserInspection(userId, inspectionId);
        Plan plan = serviceUtils.tryToFindByID(inspection.getPlans(), planId);

        inspection.getPlans().remove(plan);
        planRepository.delete(plan);
    }

    @Transactional
    public void updatePlanFromInspection(Long userId, Long inspectionId, Long planId, String newName) {
        Inspection inspection = getUserInspection(userId, inspectionId);
        Plan plan = serviceUtils.tryToFindByID(inspection.getPlans(), planId);

        plan.setName(newName);

        planRepository.save(plan);
    }

    @Transactional(readOnly = true)
    public Set<Plan> getPlans(Long userId, Long inspectionId) {
        Inspection inspection = getUserInspection(userId, inspectionId);
        return inspection.getPlans();
    }

    @Transactional(readOnly = true)
    public Plan getFullPlanInfo(Long userId, Long inspectionId, Long planId) {
        Inspection inspection = getUserInspection(userId, inspectionId);
        Plan plan = serviceUtils.tryToFindByID(inspection.getPlans(), planId);
        return plan;
    }

    @Transactional(readOnly = true)
    public StorageService.BytesWithContentType getPlanFile(Long userId, Long inspectionId, Long planId) {
        Inspection inspection = getUserInspection(userId, inspectionId);
        Plan plan = serviceUtils.tryToFindByID(inspection.getPlans(), planId);
        return storageService.getFile(BucketName.PlAN, plan.getFileUuid().toString());
    }

//    @Transactional
//    public Identifiable addPhotoToPlan(Long userId, Long inspectionId, Long planId, String name, Double x, Double y, MultipartFile file) {
//        Inspection inspection = getUserInspection(userId, inspectionId);
//        Plan plan = serviceUtils.tryToFindByID(inspection.getPlans(), planId);
//
//        UUID uuid = UUID.randomUUID();
//        PhotoPlan photo = photoMapper.mapToPhoto(name, uuid, plan, x, y);
//
//        Photo afterSave = photoRepository.save(photo);
//        storageService.saveFile(BucketName.DEFAULT_IMAGE_BUCKET, uuid.toString(), file);
//
//        return afterSave;
//    }

    /**
     * Создает или обновляет фотографию для мобильного приложения в зависимости от наличия photoId.
     * При обновлении фотографии, если есть уже в категории, то происходит отчистка в категориях
     */
    @Transactional
    public PhotoPlan updateOrCreatePhoto(Long userId, Long photoId, Long planId, PhotoCreateDto photoCreateDto, MultipartFile multipartFile) {
        if (photoId == null) {
            if (multipartFile == null || multipartFile.isEmpty()) {
                throw new MessageException(HttpStatus.BAD_REQUEST, "Can't create photo without file");
            }
            PhotoPlan photo = new PhotoPlan();
            UUID uuid = UUID.randomUUID();

            photoMapper.mapToPhoto(photo, photoCreateDto, uuid, planId);

            photoPlanRepository.save(photo);
            storageService.saveFile(BucketName.DEFAULT_IMAGE_BUCKET, uuid, multipartFile);
            return photo;
        }

        PhotoPlan photo = forThisUser(userId, photoId);

        if (photo == null) {
            throw new EntityNotFoundException("Can't find this photo for user");
        }

        // файл для модификации не может быть реальной фотки
        UUID uuid = photo.getFileUuid();
        photoMapper.mapToPhoto(photo, photoCreateDto, uuid, planId);
        photoPlanRepository.save(photo);

        if (multipartFile != null && !multipartFile.isEmpty()) {
            Optional.ofNullable(photo.getConnected()).ifPresent(l -> l.forEach(connectedPhoto -> {
                connectedPhoto.setDefectsCoords(null);
                photoRepository.save(connectedPhoto);
            }));
            storageService.saveFile(BucketName.DEFAULT_IMAGE_BUCKET, uuid, multipartFile);
        }

        return photo;
    }

    public StorageService.BytesWithContentType getPhotoFromPlan(Long userId, Long inspectionId, Long planId, Long photoId) {
        PhotoPlan plan = forThisUser(userId, photoId);
        if (plan.getFileUuid() != null) {
            return storageService.getFile(BucketName.DEFAULT_IMAGE_BUCKET, plan.getFileUuid().toString());
        }
        return null;
    }

    @Transactional
    public void deletePhotoFromPlan(Long userId, Long photoId) {
        PhotoPlan photo = forThisUser(userId, photoId);
        if (photo != null) {
            photoPlanRepository.delete(photo);
        }
    }


    @Transactional
    public Identifiable copyPhotoToCategoryFromPlan(Long userId, Long inspectionId, Long planId, Long photoId,
                                                    Long categoryId) {
        // TODO оптимизация запроса
        Inspection inspection = getInspectionIfExistForUser(inspectionId, userId);
        Plan plan = serviceUtils.tryToFindByID(inspection.getPlans(), planId);
        PhotoPlan photoPlan = serviceUtils.tryToFindByID(plan.getPhotos(), photoId);
        Category category = serviceUtils.tryToFindByID(inspection.getCategories(), categoryId);


        Photo photo = photoMapper.mapToPhoto(photoPlan, category);
        return photoRepository.save(photo);
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
        Photo photo = photoMapper.mapToPhoto(multipartFile.getOriginalFilename(), uuid, category);
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

    public boolean addTaskForCreatingDocument(Long inspectionId, Long userId) {
        Inspection inspection = getInspectionIfExistForUser(inspectionId, userId);
        User user = userRepository.findById(userId).orElse(null);

        // TOOD в случае если документ уже генерируется, то оповещать пользователя, что заново генерировать нельзя
        if (inspection.getStatus() == ProgressingStatus.WAIT_ANALYZE) return false;

        return documentService.addInspectionInQueueToProcess(inspection, user);
    }


    public void sendAllPhotosToAnalyze(Long inspectionId, Long userId) {
        boolean forThisUser = inspectionRepository.existsByIdAndUsersId(inspectionId, userId);
        if (!forThisUser) return;

        Inspection inspection = inspectionFetcherEngine.getInspectionWithSubEntities(inspectionId);

        Optional.ofNullable(inspection).map(Inspection::getCategories)
                .ifPresent(cat -> cat.forEach(category -> category.getPhotos().stream()
                        .filter(photo -> photo.getDefectsCoords() == null)
                        .forEach(analyzeService::fetchAnalyzeAndSave)));
    }

    // --------------------------------------- create-document -----------------------------------------
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

    public Inspection getInspectionIfExistForUser(Long inspectionId, Long userId) {
        Inspection inspection = inspectionRepository.findByUsersIdAndId(userId, inspectionId);
        if (inspection == null) {
            throw new EntityNotFoundException(String.format("No such inspection with id %s for this user", inspectionId));
        }
        return inspection;
    }


    private PhotoPlan forThisUser(Long userId, Long photoId) {
        PhotoPlan photo = photoPlanRepository.findById(photoId).orElseThrow(EntityNotFoundException::new);
        Set<User> usersSet1 = Optional.ofNullable(photo.getPlan()).map(Plan::getInspection)
                .map(Inspection::getUsers).orElse(Set.of());

        if (!usersSet1.stream().map(User::getId).toList().contains(userId)) {
            return null;
        }
        return photo;
    }
}
