package com.service.inspection.mapper;

import com.service.inspection.dto.IdentifiableDto;
import com.service.inspection.dto.inspection.InspectionPlansDto;
import com.service.inspection.dto.inspection.PlanDto;
import com.service.inspection.entities.Photo;
import com.service.inspection.entities.PhotoPlan;
import com.service.inspection.entities.Plan;
import com.service.inspection.mapper.document.TableMapperImpl;
import com.service.inspection.utils.CommonUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Comparator;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PlanMapperImpl.class, CommonMapperImpl.class})
class PlanMapperTest {

    @Autowired
    private PlanMapper planMapper;

    @Test
    void testMapper() {
        Plan plan = new Plan();
        plan.setId(1L);
        plan.setName("test");
        plan.setFileUuid(UUID.randomUUID());

        Plan plan1 = new Plan();
        plan1.setId(2L);
        plan1.setName("test2");
        plan1.setFileUuid(UUID.randomUUID());

        InspectionPlansDto inspectionPlansDto = planMapper.mapToInspectionPlanDto(Set.of(plan, plan1));

        assertThat(inspectionPlansDto.getPlans().stream().sorted(Comparator.comparingLong(IdentifiableDto::getId))).element(0).satisfies(planDto -> {
            assertThat(planDto.getId()).isEqualTo(plan.getId());
            assertThat(planDto.getName()).isEqualTo(plan.getName());
        });
        assertThat(inspectionPlansDto.getPlans().stream().sorted(Comparator.comparingLong(IdentifiableDto::getId))).element(1).satisfies(planDto -> {
            assertThat(planDto.getId()).isEqualTo(plan1.getId());
            assertThat(planDto.getName()).isEqualTo(plan1.getName());
        });
    }

    @Test
    void testMapToInspectionPlanDto() {
        PhotoPlan photo = new PhotoPlan();
        photo.setId(1L);
        photo.setName("test");

        PhotoPlan photo1 = new PhotoPlan();
        photo1.setId(2L);
        photo1.setName("test2");

        Plan plan1 = new Plan();
        plan1.setId(2L);
        plan1.setName("test2");
        plan1.setFileUuid(UUID.randomUUID());
        plan1.setPhotos(Set.of(photo, photo1));

        PlanDto planDto = planMapper.mapToPlanDto(plan1);

        assertThat(planDto.getId()).isEqualTo(plan1.getId());
        assertThat(planDto.getName()).isEqualTo(plan1.getName());

        assertThat(planDto.getPhotos().stream().sorted(Comparator.comparingLong(IdentifiableDto::getId))).element(0).satisfies(photoDto -> {
            assertThat(photoDto.getId()).isEqualTo(photo.getId());
            assertThat(photoDto.getName()).isEqualTo(photo.getName());
        });

        assertThat(planDto.getPhotos().stream().sorted(Comparator.comparingLong(IdentifiableDto::getId))).element(1).satisfies(photoDto -> {
            assertThat(photoDto.getId()).isEqualTo(photo1.getId());
            assertThat(photoDto.getName()).isEqualTo(photo1.getName());
        });
    }
}
