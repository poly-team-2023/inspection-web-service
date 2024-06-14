package com.service.inspection.mapper.document;

import com.service.inspection.document.DocumentModel;
import com.service.inspection.document.model.*;
import com.service.inspection.entities.*;
import com.service.inspection.mapper.SenderMapper;
import com.service.inspection.service.AnalyzeService;
import com.service.inspection.service.LicenseService;
import com.service.inspection.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.mapstruct.Named;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring",
        uses = {ImageMapper.class, TableMapper.class}
)
@Slf4j
public abstract class DocumentMapper {

    @Autowired
    private CommonUtils utils;

    @Autowired
    private AnalyzeService documentModelService;

    @Autowired
    private SenderMapper senderMapper;

    @Mapping(target = "company", source = "inspection.company")
    @Mapping(source = "inspection.name", target = "projectName")
    @Mapping(source = "inspection.reportName", target = "reportName", defaultValue = "Технический отчет об обследовании")
    @Mapping(source = "inspection.script", target = "script")
    @Mapping(target = "categories", ignore = true)
    @Mapping(source = "inspection.employer", target = "employer")
    @Mapping(source = "user.equipment", target = "equipment.table")
    public abstract DocumentModel mapToDocumentModel(Inspection inspection, User user, @Context List<CompletableFuture<Void>> futureResult);

    @Mapping(source = "processedPhotos", target = "photos")
    @Mapping(source = "category.numberOfCategory", target = "categoryNum", defaultValue = "0L")
    @Mapping(source = "processedPhotos", target = "defects", qualifiedByName = "processedPhotos")
    @Mapping(source = "processedPhotos", target = "defectsWithPhotos", dependsOn = "defects")
    public abstract CategoryModel mapToCategoryModel(Category category, List<ImageModelWithDefects> processedPhotos);

    @Named("processedPhotos")
    public Set<String> processedPhotos(List<ImageModelWithDefects> defectsModel) {
        if (defectsModel == null) return null;
        return defectsModel.stream().flatMap(x -> x.getDefects().stream())
                .map(DefectModel::getName).map(utils::toHumanReadable).collect(Collectors.toSet());
    }


    public Map<String, CategoryDefectsModel> processedPhotosMap(List<ImageModelWithDefects> defectsModel) {
        Map<String, CategoryDefectsModel> map = new HashMap<>();

        for (ImageModelWithDefects image : defectsModel) {
            if (image == null || image.getDefects() == null) continue;
            for (DefectModel defect : image.getDefects()) {
                map.putIfAbsent(defect.getName(), new CategoryDefectsModel(new LinkedHashSet<>(), ""));
                map.get(defect.getName()).getPhotoNums().add(image.getPhotoNum());
            }
        }
        return map;
    }

    @AfterMapping
    public void mappingAsyncPhotoFields(@MappingTarget DocumentModel documentModel, Inspection inspection, User user,
                                        @Context List<CompletableFuture<Void>> futureResult) {

        List<Category> categories = inspection.getCategories();

        // --------- сортировка категорий в соответствии с id ---------
        categories.sort(Comparator.comparingLong(Category::getId));
        for (int i = 0; i < categories.size(); i++) {
            categories.get(i).setNumberOfCategory(i + 1);
        }
        // ------------------------------------------------------------


        List<CompletableFuture<Void>> categoryFutureContext = Collections.synchronizedList(new ArrayList<>());
        long lastPhotosCount = 1L;

        for (Category category : categories) {
            Hibernate.initialize(category.getPhotos());
            // часть генерации с анализом фотографий
            categoryFutureContext.add(
                    documentModelService.processAllPhotosAsync(category.getPhotos(), lastPhotosCount).thenAccept(x -> {

                        documentModel.addCategory(this.mapToCategoryModel(category, x));
                        log.debug("Add category {} to inspection {}", category.getId(), inspection.getId());
                    })
            );
            lastPhotosCount += category.getPhotos().size();
        }

        // часть генерации с GPT
        CompletableFuture<Void> modelWithUpdatedByGptModel = CompletableFuture.allOf(categoryFutureContext.toArray(new CompletableFuture[0]))
                .thenCompose(f -> documentModelService.analyzeAllDefects(documentModel, inspection.getId()))
                .thenAccept(data -> {
                    // добавил именно тут, что сохранить нумерацию в таблице !
                    documentModel.getCategories().sort(Comparator.comparing(CategoryModel::getCategoryNum));
                    senderMapper.updateDocumentModelWithGpt(documentModel, data);
                });


        futureResult.add(modelWithUpdatedByGptModel);

        Optional.ofNullable(inspection.getMainPhotoUuid())
                .ifPresent(uuid -> futureResult.add(documentModelService.fetchPhoto(uuid)
                        .thenAccept(documentModel::setMainPhoto)));

        Optional.ofNullable(inspection.getCompany()).map(Company::getLogoUuid)
                .ifPresent(logoUuid -> futureResult.add(documentModelService.fetchPhoto(logoUuid)
                .thenAccept(x -> documentModel.getCompany().setLogo(x))));

        Optional.ofNullable(inspection.getEmployer()).map(Employer::getSignatureUuid)
                .ifPresent(signUuid -> futureResult.add(documentModelService.fetchPhoto(signUuid)
                .thenAccept(x -> documentModel.getEmployer().setScript(x))));

        Optional.ofNullable(inspection.getCompany()).map(Company::getFilesSro).ifPresent(files -> {
            files.stream().map(FileEntity::getFileUuid).forEach(uuid -> futureResult.add(documentModelService.fetchPhoto(uuid)
                    .thenAccept(x -> documentModel.getCompany().getFiles().add(x))));
        });

        Optional.ofNullable(inspection.getCompany()).map(Company::getLicenses).ifPresent(licenses -> {
            licenses.forEach(x -> Optional.ofNullable(x.getFiles()).ifPresent(files -> {
                files.stream().map(FileEntity::getFileUuid).forEach(uuid -> futureResult.add(documentModelService.fetchPhoto(uuid)
                        .thenAccept(y -> documentModel.getCompany().getFiles().add(y))));
            }));
        });

        Optional.ofNullable(user.getEquipment()).ifPresent(equipment -> {
            equipment.forEach(x -> Optional.ofNullable(x.getFiles()).ifPresent(files -> {
                files.stream().map(FileEntity::getFileUuid).forEach(uuid -> futureResult.add(documentModelService.fetchPhoto(uuid)
                        .thenAccept(y -> documentModel.getEquipment().getScans().add(y))));
            }));
        });
    }

    abstract CompanyModel companyModel(Company company, @Context List<CompletableFuture<Void>> futureResult);

    @AfterMapping
    public void mappingCompanyPhotoAsync(@MappingTarget CompanyModel companyModel, Company company,
                                         @Context List<CompletableFuture<Void>> futureResult) {
        UUID uuid = company.getLogoUuid();
        if (uuid != null) {
            futureResult.add(documentModelService.fetchPhoto(uuid).thenAccept(companyModel::setLogo));
        }
    }
}
