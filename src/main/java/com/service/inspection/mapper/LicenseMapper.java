package com.service.inspection.mapper;

import com.service.inspection.dto.license.GetLicenseDto;
import com.service.inspection.dto.license.LicenseDto;
import com.service.inspection.entities.License;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"
)
public interface LicenseMapper {

    License mapToLicense(LicenseDto dto);

    GetLicenseDto mapToDto(License license);
}
