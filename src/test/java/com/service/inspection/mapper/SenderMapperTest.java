package com.service.inspection.mapper;

import com.service.inspection.document.DocumentModel;
import com.service.inspection.document.model.CategoryDefectsModel;
import com.service.inspection.document.model.CategoryModel;
import com.service.inspection.document.model.DefectModel;
import com.service.inspection.document.model.ImageModelWithDefects;
import com.service.inspection.dto.document.GptReceiverDto;
import com.service.inspection.dto.document.GptSenderDto;
import com.service.inspection.mapper.document.TableMapperImpl;
import com.service.inspection.utils.CommonUtils;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SenderMapperImpl.class, TableMapperImpl.class, CommonUtils.class})
class SenderMapperTest {

    @Autowired
    private SenderMapper senderMapper;



    @Test
    void testSenderMapperMapToGptSenderDto() {
        ImageModelWithDefects imageModelWithDefects = new ImageModelWithDefects();
        imageModelWithDefects.setPhotoNum(1L);
        imageModelWithDefects.setImageTitle("photo1");
        imageModelWithDefects.setDefects(List.of(new DefectModel("Defect1"), new DefectModel("Defect2"), new DefectModel("Defect1")));

        ImageModelWithDefects imageModelWithDefects1 = new ImageModelWithDefects();
        imageModelWithDefects1.setPhotoNum(2L);
        imageModelWithDefects1.setImageTitle("photo2");
        imageModelWithDefects1.setDefects(List.of(new DefectModel("Defect2"), new DefectModel("Defect3"), new DefectModel("Defect1")));

        CategoryModel categoryModel = new CategoryModel();
        categoryModel.setName("CategoryModel");
        categoryModel.setId(1L);
        categoryModel.setPhotos(List.of(imageModelWithDefects, imageModelWithDefects1));

        ImageModelWithDefects imageModelWithDefects3 = new ImageModelWithDefects();
        imageModelWithDefects3.setPhotoNum(3L);
        imageModelWithDefects3.setImageTitle("photo3");
        imageModelWithDefects3.setDefects(List.of(new DefectModel("Defect5"), new DefectModel("Defect5"), new DefectModel("Defect5")));

        ImageModelWithDefects imageModelWithDefects4 = new ImageModelWithDefects();
        imageModelWithDefects4.setPhotoNum(4L);
        imageModelWithDefects4.setImageTitle("photo4");
        imageModelWithDefects4.setDefects(List.of(new DefectModel("Defect5"), new DefectModel("Defect6"), new DefectModel("Defect1")));

        CategoryModel categoryModel2 = new CategoryModel();
        categoryModel2.setName("CategoryModel2");
        categoryModel2.setId(2L);
        categoryModel2.setPhotos(List.of(imageModelWithDefects3, imageModelWithDefects4));

        DocumentModel documentModel = new DocumentModel();
        documentModel.setCategories(List.of(categoryModel, categoryModel2));

        GptSenderDto result = senderMapper.mapToGptSenderDto(documentModel);

        assertThat(result.getCategories()).hasSize(2);

        assertThat(result.getCategories()).element(0).satisfies(categoryDto -> {
            assertThat(categoryDto.getName()).isEqualTo(categoryModel.getName());
            assertThat(categoryDto.getId()).isEqualTo(categoryModel.getId());
            assertThat(categoryDto.getDefects()).extracting("name", "count")
                    .containsExactlyInAnyOrder(Tuple.tuple("Defect1", 3L), Tuple.tuple("Defect2", 2L), Tuple.tuple("Defect3", 1L));
        });

        assertThat(result.getCategories()).element(1).satisfies(categoryDto -> {
            assertThat(categoryDto.getName()).isEqualTo(categoryModel2.getName());
            assertThat(categoryDto.getId()).isEqualTo(categoryModel2.getId());
            assertThat(categoryDto.getDefects()).extracting("name", "count")
                    .containsExactlyInAnyOrder(Tuple.tuple("Defect5", 4L), Tuple.tuple("Defect6", 1L), Tuple.tuple("Defect1", 1L));
        });
    }

    @Test
    void testSenderMapperToModelFromGptRecieveDto() {
        GptReceiverDto.GptReceiverDefectsDto gptReceiverDefectDto = new GptReceiverDto.GptReceiverDefectsDto();
        gptReceiverDefectDto.setName("Defect1");
        gptReceiverDefectDto.setEstimation("estimation1");
        gptReceiverDefectDto.setRecommendation("recommendation1");

        GptReceiverDto.GptReceiverDefectsDto gptReceiverDefectDto2 = new GptReceiverDto.GptReceiverDefectsDto();
        gptReceiverDefectDto2.setName("Defect2");
        gptReceiverDefectDto2.setEstimation("estimation2");
        gptReceiverDefectDto2.setRecommendation("recommendation2");

        GptReceiverDto.GptReceiverDefectsDto gptReceiverDefectDto3 = new GptReceiverDto.GptReceiverDefectsDto();
        gptReceiverDefectDto3.setName("Defect3");
        gptReceiverDefectDto3.setEstimation("estimation3");
        gptReceiverDefectDto3.setRecommendation("recommendation3");

        GptReceiverDto.GptReceiverCategoryDto gptReceiverCategoryDto = new GptReceiverDto.GptReceiverCategoryDto();
        gptReceiverCategoryDto.setId(1L);
        gptReceiverCategoryDto.setEstimation("category estimation 1");
        gptReceiverCategoryDto.setRecommendation("category recommendation 1");
        gptReceiverCategoryDto.setDefects(List.of(gptReceiverDefectDto, gptReceiverDefectDto2));

        GptReceiverDto.GptReceiverCategoryDto gptReceiverCategoryDto2 = new GptReceiverDto.GptReceiverCategoryDto();
        gptReceiverCategoryDto2.setId(2L);
        gptReceiverCategoryDto2.setEstimation("category estimation 2");
        gptReceiverCategoryDto2.setRecommendation("category recommendation 2");
        gptReceiverCategoryDto2.setDefects(List.of(gptReceiverDefectDto3));

        GptReceiverDto.GptBuildingInfo gptBuildingInfo = new GptReceiverDto.GptBuildingInfo();
        gptBuildingInfo.setEstimation("building estimation 1");
        gptBuildingInfo.setRecommendation("building recommendation 1");
        gptBuildingInfo.setCategories(List.of(gptReceiverCategoryDto, gptReceiverCategoryDto2));

        GptReceiverDto gptReceiverDto = new GptReceiverDto();
        gptReceiverDto.setBuilding(gptBuildingInfo);


        Map<String, CategoryDefectsModel> map = new HashMap<>();
        map.putIfAbsent("Defect1", new CategoryDefectsModel(Set.of(1L, 2L, 3L), ""));
        map.putIfAbsent("Defect2", new CategoryDefectsModel(Set.of(3L, 5L), ""));

        CategoryModel categoryModel = new CategoryModel();
        categoryModel.setId(1L);
        categoryModel.setDefectsWithPhotos(map);

        Map<String, CategoryDefectsModel> map1 = new HashMap<>();
        map1.putIfAbsent("Defect3", new CategoryDefectsModel(Set.of(4L), ""));

        CategoryModel categoryModel1 = new CategoryModel();
        categoryModel1.setId(2L);
        categoryModel1.setDefectsWithPhotos(map1);


        DocumentModel documentModel = new DocumentModel();
        documentModel.setCategories(List.of(categoryModel, categoryModel1));

        senderMapper.updateDocumentModelWithGpt(documentModel, gptReceiverDto);

        assertThat(documentModel.getEstimation()).isEqualTo(gptReceiverDto.getBuilding().getEstimation());
        assertThat(documentModel.getRecommendation()).isEqualTo(gptReceiverDto.getBuilding().getRecommendation());

        assertThat(documentModel.getCategories()).element(0).satisfies(categoryDto -> {
            assertThat(categoryDto.getEstimation()).isEqualTo(gptReceiverCategoryDto.getEstimation());

            assertThat(categoryDto.getDefectsWithPhotos().get("Defect1").getRecommendation()).isEqualTo(gptReceiverDefectDto.getRecommendation());
            assertThat(categoryDto.getDefectsWithPhotos().get("Defect2").getRecommendation()).isEqualTo(gptReceiverDefectDto2.getRecommendation());
        });

        assertThat(documentModel.getCategories()).element(1).satisfies(categoryDto -> {
            assertThat(categoryDto.getEstimation()).isEqualTo(gptReceiverCategoryDto2.getEstimation());

            assertThat(categoryDto.getDefectsWithPhotos().get("Defect3").getRecommendation()).isEqualTo(gptReceiverDefectDto3.getRecommendation());
        });
    }
}
