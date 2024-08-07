package com.service.inspection.mapper;

import com.service.inspection.dto.IdentifiableDto;
import com.service.inspection.dto.NamedDto;
import com.service.inspection.entities.Identifiable;
import com.service.inspection.entities.Named;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.Collection;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"
)
public interface CommonMapper {
    IdentifiableDto mapToIdentifiableDto(Identifiable identifiable);

    NamedDto mapToNamedDto(Named named);

    @org.mapstruct.Named("namedToNamedDto")
    Collection<NamedDto> mapToNamedDto(Collection<Named> named);
}
