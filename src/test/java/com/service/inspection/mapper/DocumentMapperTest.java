package com.service.inspection.mapper;

import com.deepoove.poi.data.ByteArrayPictureRenderData;
import com.deepoove.poi.data.PictureRenderData;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.service.inspection.document.DocumentModel;
import com.service.inspection.document.model.CategoryDefectsModel;
import com.service.inspection.document.model.DefectModel;
import com.service.inspection.document.model.ImageModel;
import com.service.inspection.document.model.ImageModelWithDefects;
import com.service.inspection.dto.document.GptReceiverDto;
import com.service.inspection.entities.*;
import com.service.inspection.entities.enums.BuildingType;
import com.service.inspection.mapper.document.DocumentMapper;
import com.service.inspection.mapper.document.DocumentMapperImpl;
import com.service.inspection.mapper.document.ImageMapperImpl;
import com.service.inspection.mapper.document.TableMapperImpl;
import com.service.inspection.service.AnalyzeService;
import com.service.inspection.utils.CommonUtils;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DocumentMapperImpl.class, ImageMapperImpl.class,
        TableMapperImpl.class, SenderMapperImpl.class, CommonUtils.class})
class DocumentMapperTest {

    @Autowired
    private DocumentMapper documentMapper;

    @MockBean
    private AnalyzeService documentModelService;

    @Test
    void shouldMapToDocumentModelSuccessfully() {
        UUID mainPhotoUuid = UUID.randomUUID();
        UUID employerScriptUuid = UUID.randomUUID();
        UUID companyLogoUuid = UUID.randomUUID();

        UUID photo1Uuid = UUID.randomUUID();
        UUID photo2Uuid = UUID.randomUUID();
        UUID photo3Uuid = UUID.randomUUID();

        Equipment equipment = new Equipment();
        equipment.setName("equipment 1");
        equipment.setId(1L);
        equipment.setSerialNumber("testSerialNumber");
        equipment.setVerificationDate(LocalDate.now());
        equipment.setVerificationDate(LocalDate.now());
        equipment.setVerificationNumber("123451");

        Photo photo = new Photo();
        photo.setName("photo1");
        photo.setId(5L);
        photo.setFileUuid(photo1Uuid);

        Photo photo1 = new Photo();
        photo1.setName("photo2");
        photo1.setId(6L);
        photo1.setFileUuid(photo2Uuid);

        Photo photo2 = new Photo();
        photo2.setName("photo3");
        photo2.setId(7L);
        photo2.setFileUuid(photo3Uuid);

        List<Photo> photos = new ArrayList<>();
        photos.add(photo);
        photos.add(photo1);

        List<Photo> photos1 = new ArrayList<>();
        photos1.add(photo2);

        Category category = new Category();
        category.setId(4L);
        category.setName("category1");
        category.setPhotos(photos);

        Category category2 = new Category();
        category2.setId(8L);
        category2.setName("category2");
        category2.setPhotos(photos1);

        List<Category> categories = new ArrayList<>();
        categories.add(category);
        categories.add(category2);

        Employer employer = new Employer();
        employer.setId(3L);
        employer.setName("Employer1");
        employer.setPositionName("Test position");
        employer.setSignatureUuid(employerScriptUuid);

        Company company = new Company();
        company.setId(2L);
        company.setName("SelectedCompany");
        company.setLegalAddress("Legal address");
        company.setLogoUuid(companyLogoUuid);
        company.setCity("SaintP");

        Inspection inspection = new Inspection();
        inspection.setId(1L);
        inspection.setName("Test inspection");
        inspection.setReportName("Name");
        inspection.setScript("Test inspection script");
        inspection.setAddress("Address");
        inspection.setBuildingType(BuildingType.CULTURE);
        inspection.setMainPhotoUuid(mainPhotoUuid);
        inspection.setCompany(company);
        inspection.setEmployer(employer);
        inspection.setCategories(categories);

        User user = new User();
        user.setEquipment(List.of(equipment));

        List<CompletableFuture<Void>> completableFutures = new ArrayList<>();

        ImageModelWithDefects imageModelWithDefects = new ImageModelWithDefects();
        imageModelWithDefects.setImageTitle("Image 1 after analyze");
        imageModelWithDefects.setDefects(List.of(new DefectModel("defect1"), new DefectModel("defect2")));
        imageModelWithDefects.setPhotoNum(1L);

        ImageModelWithDefects imageModelWithDefects2 = new ImageModelWithDefects();
        imageModelWithDefects2.setImageTitle("Image 2 after analyze");
        imageModelWithDefects2.setDefects(List.of(new DefectModel("defect3"), new DefectModel("defect1")));
        imageModelWithDefects2.setPhotoNum(2L);

        ImageModelWithDefects imageModelWithDefects3 = new ImageModelWithDefects();
        imageModelWithDefects3.setImageTitle("Image 3 after analyze");
        imageModelWithDefects3.setDefects(List.of(new DefectModel("defect4"), new DefectModel("defect41"), new DefectModel("defect42")));
        imageModelWithDefects3.setPhotoNum(3L);

        ImageModel imageModel = new ImageModel();
        imageModel.setImageTitle("Test image title");
        imageModel.setImage(new ByteArrayPictureRenderData("123456".getBytes()));

        ImageModel imageModel1 = new ImageModel();
        imageModel1.setImageTitle("Test 1 image title");
        imageModel1.setImage(new ByteArrayPictureRenderData("12345".getBytes()));

        ImageModel imageModel2 = new ImageModel();
        imageModel2.setImageTitle("Test 2 image title");
        imageModel2.setImage(new ByteArrayPictureRenderData("1234".getBytes()));

        when(documentModelService.processAllPhotosAsync(category.getPhotos(), 1L))
                .thenReturn(CompletableFuture.completedFuture(List.of(imageModelWithDefects, imageModelWithDefects2)));

        when(documentModelService.processAllPhotosAsync(category2.getPhotos(), 3L))
                .thenReturn(CompletableFuture.completedFuture(List.of(imageModelWithDefects3)));

        when(documentModelService.fetchPhoto(companyLogoUuid))
                .thenReturn(CompletableFuture.completedFuture(imageModel));
        when(documentModelService.fetchPhoto(employerScriptUuid))
                .thenReturn(CompletableFuture.completedFuture(imageModel1));
        when(documentModelService.fetchPhoto(mainPhotoUuid))
                .thenReturn(CompletableFuture.completedFuture(imageModel2));

        GptReceiverDto.GptReceiverDefectsDto gptReceiverDefectsDto = new GptReceiverDto.GptReceiverDefectsDto();
        gptReceiverDefectsDto.setName("defect1");
        gptReceiverDefectsDto.setEstimation("defect1Estimation");
        gptReceiverDefectsDto.setRecommendation("defect1Recommendation");

        GptReceiverDto.GptReceiverDefectsDto gptReceiverDefectsDto2 = new GptReceiverDto.GptReceiverDefectsDto();
        gptReceiverDefectsDto2.setName("defect2");
        gptReceiverDefectsDto2.setEstimation("defect2Estimation");
        gptReceiverDefectsDto2.setRecommendation("defect2Recommendation");

        GptReceiverDto.GptReceiverDefectsDto gptReceiverDefectsDto3 = new GptReceiverDto.GptReceiverDefectsDto();
        gptReceiverDefectsDto3.setName("defect3");
        gptReceiverDefectsDto3.setEstimation("defect3Estimation");
        gptReceiverDefectsDto3.setRecommendation("defect3Recommendation");

        GptReceiverDto.GptReceiverDefectsDto gptReceiverDefectsDto4 = new GptReceiverDto.GptReceiverDefectsDto();
        gptReceiverDefectsDto4.setName("defect4");
        gptReceiverDefectsDto4.setEstimation("defect4Estimation");
        gptReceiverDefectsDto4.setRecommendation("defect4Recommendation");

        GptReceiverDto.GptReceiverDefectsDto gptReceiverDefectsDto5 = new GptReceiverDto.GptReceiverDefectsDto();
        gptReceiverDefectsDto5.setName("defect41");
        gptReceiverDefectsDto5.setEstimation("defect41Estimation");
        gptReceiverDefectsDto5.setRecommendation("defect41Recommendation");

        GptReceiverDto.GptReceiverDefectsDto gptReceiverDefectsDto6 = new GptReceiverDto.GptReceiverDefectsDto();
        gptReceiverDefectsDto6.setName("defect42");
        gptReceiverDefectsDto6.setEstimation("defect42Estimation");
        gptReceiverDefectsDto6.setRecommendation("defect42Recommendation");

        GptReceiverDto.GptReceiverCategoryDto gptReceiverCategoryDto = new GptReceiverDto.GptReceiverCategoryDto();
        gptReceiverCategoryDto.setName(category.getName());
        gptReceiverCategoryDto.setId(category.getId());
        gptReceiverCategoryDto.setDefects(List.of(gptReceiverDefectsDto, gptReceiverDefectsDto2, gptReceiverDefectsDto3));

        GptReceiverDto.GptReceiverCategoryDto gptReceiverCategoryDto1 = new GptReceiverDto.GptReceiverCategoryDto();
        gptReceiverCategoryDto1.setName(category2.getName());
        gptReceiverCategoryDto1.setId(category2.getId());
        gptReceiverCategoryDto1.setDefects(List.of(gptReceiverDefectsDto4, gptReceiverDefectsDto5, gptReceiverDefectsDto6));

        GptReceiverDto.GptBuildingInfo gptBuildingInfo = new GptReceiverDto.GptBuildingInfo();
        gptBuildingInfo.setEstimation("Estimation");
        gptBuildingInfo.setRecommendation("Reccomendation");
        gptBuildingInfo.setCategories(List.of(gptReceiverCategoryDto, gptReceiverCategoryDto1));

        GptReceiverDto gptReceiverDto = new GptReceiverDto();
        gptReceiverDto.setBuilding(gptBuildingInfo);

        when(documentModelService.analyzeAllDefects(any(), eq(inspection.getId())))
                .thenReturn(CompletableFuture.completedFuture(gptReceiverDto));

        DocumentModel documentModel = documentMapper.mapToDocumentModel(inspection, user, completableFutures);
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();

        assertThat(completableFutures).isNotEmpty();

        assertThat(documentModel.getYear()).isEqualTo(Year.now().getValue());
        assertThat(documentModel.getReportName()).isEqualTo(inspection.getReportName());
        assertThat(documentModel.getProjectName()).isEqualTo(inspection.getName());
        assertThat(documentModel.getRecommendation()).isEqualTo(gptBuildingInfo.getRecommendation());
        assertThat(documentModel.getEstimation()).isEqualTo(gptBuildingInfo.getEstimation());
        assertThat(documentModel.getMainPhoto()).isEqualTo(imageModel2);
        assertThat(documentModel.getScript()).isEqualTo(inspection.getScript());

        assertThat(documentModel.getEquipment()).isNotNull();
        assertThat(documentModel.getCategoriesDefectsTable()).isNotNull();

        assertThat(documentModel.getEmployer()).satisfies(emp -> {
            assertThat(emp).extracting("positionName", "name")
                    .containsExactlyInAnyOrder(employer.getPositionName(), employer.getName());
            assertThat(emp.getScript()).isNotNull();
        });

        assertThat(documentModel.getCategories()).element(0).satisfies(categoryModel -> {
            assertThat(categoryModel.getId()).isEqualTo(category.getId());
            assertThat(categoryModel.getName()).isEqualTo(category.getName());
            assertThat(categoryModel.getDefects()).map(String::toLowerCase)
                    .containsAll(Stream.of(gptReceiverDefectsDto, gptReceiverDefectsDto2, gptReceiverDefectsDto3)
                            .map(GptReceiverDto.GptReceiverDefectsDto::getName).toList());
            assertThat(categoryModel.getEstimation()).isEqualTo(gptReceiverCategoryDto.getEstimation());
            assertThat(categoryModel.getRecommendation()).isEqualTo(gptReceiverCategoryDto.getRecommendation());

            assertThat(categoryModel.getDefectsWithPhotos()).satisfies(map -> {
                assertThat(map.get(gptReceiverDefectsDto.getName())).isNotNull()
                        .satisfies(categoryDefectsModel -> {
                            assertThat(categoryDefectsModel.getRecommendation()).isEqualTo(gptReceiverDefectsDto.getRecommendation());
                            assertThat(categoryDefectsModel.getPhotoNums()).containsAll(Set.of(1L, 2L));
                        });
                assertThat(map.get(gptReceiverDefectsDto2.getName())).isNotNull()
                        .satisfies(categoryDefectsModel -> {
                            assertThat(categoryDefectsModel.getRecommendation()).isEqualTo(gptReceiverDefectsDto2.getRecommendation());
                            assertThat(categoryDefectsModel.getPhotoNums()).containsAll(Set.of(1L));
                        });
                assertThat(map.get(gptReceiverDefectsDto3.getName())).isNotNull()
                        .satisfies(categoryDefectsModel -> {
                            assertThat(categoryDefectsModel.getRecommendation()).isEqualTo(gptReceiverDefectsDto3.getRecommendation());
                            assertThat(categoryDefectsModel.getPhotoNums()).containsAll(Set.of(2L));
                        });
            });
        });

        assertThat(documentModel.getCategories()).element(1).satisfies(categoryModel -> {
            assertThat(categoryModel.getId()).isEqualTo(category2.getId());
            assertThat(categoryModel.getName()).isEqualTo(category2.getName());
            assertThat(categoryModel.getDefects()).map(String::toLowerCase)
                    .containsAll(Stream.of(gptReceiverDefectsDto4, gptReceiverDefectsDto5, gptReceiverDefectsDto6)
                            .map(GptReceiverDto.GptReceiverDefectsDto::getName).toList());
            assertThat(categoryModel.getEstimation()).isEqualTo(gptReceiverCategoryDto1.getEstimation());
            assertThat(categoryModel.getRecommendation()).isEqualTo(gptReceiverCategoryDto1.getRecommendation());

            assertThat(categoryModel.getDefectsWithPhotos()).satisfies(map -> {
                assertThat(map.get(gptReceiverDefectsDto4.getName())).isNotNull()
                        .satisfies(categoryDefectsModel -> {
                            assertThat(categoryDefectsModel.getRecommendation()).isEqualTo(gptReceiverDefectsDto4.getRecommendation());
                            assertThat(categoryDefectsModel.getPhotoNums()).containsAll(Set.of(3L));
                        });
                assertThat(map.get(gptReceiverDefectsDto5.getName())).isNotNull()
                        .satisfies(categoryDefectsModel -> {
                            assertThat(categoryDefectsModel.getRecommendation()).isEqualTo(gptReceiverDefectsDto5.getRecommendation());
                            assertThat(categoryDefectsModel.getPhotoNums()).containsAll(Set.of(3L));
                        });
                assertThat(map.get(gptReceiverDefectsDto6.getName())).isNotNull()
                        .satisfies(categoryDefectsModel -> {
                            assertThat(categoryDefectsModel.getRecommendation()).isEqualTo(gptReceiverDefectsDto6.getRecommendation());
                            assertThat(categoryDefectsModel.getPhotoNums()).containsAll(Set.of(3L));
                        });
            });
        });
    }
}
