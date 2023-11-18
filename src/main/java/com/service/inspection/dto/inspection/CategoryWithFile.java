package com.service.inspection.dto.inspection;

import com.service.inspection.dto.NamedDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CategoryWithFile extends NamedDto {
    private Set<PhotoDto> photos;

    public static class PhotoDto extends NamedDto {

    }
}
