package com.service.inspection.mapper;

import com.service.inspection.dto.NamedDto;
import com.service.inspection.dto.inspection.InspectionPlansDto;
import com.service.inspection.dto.inspection.PlanDto;
import com.service.inspection.entities.Photo;
import com.service.inspection.entities.Plan;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring", uses = {CommonMapper.class}
)
public abstract class PlanMapper {

    @Autowired
    CommonMapper commonMapper;

    public InspectionPlansDto mapToInspectionPlanDto(Set<Plan> plans) {
        return mapToInspectionPlanDto(1L, plans);
    }

    @Mapping(source = "plans", target = "plans", qualifiedByName = "namedToNamedDto")
    abstract InspectionPlansDto mapToInspectionPlanDto(Long value, Set<Plan> plans);

    public abstract PlanDto mapToPlanDto(Plan plan);

    public abstract List<NamedDto> mapToPhotos(Set<Photo> photos);

    @Named("namedToNamedDto")
    List<NamedDto> planSetToNamedDtoList(Set<Plan> set) {
        if ( set == null ) {
            return null;
        }

        List<NamedDto> list = new ArrayList<NamedDto>( set.size() );
        for ( Plan plan : set ) {

            list.add( commonMapper.mapToNamedDto(plan) );
        }

        return list;
    }
}
