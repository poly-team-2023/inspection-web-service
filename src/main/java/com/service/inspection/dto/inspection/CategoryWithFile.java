package com.service.inspection.dto.inspection;

import com.service.inspection.dto.NamedDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryWithFile extends NamedDto {
    private Set<PhotoDto> photos;

    @NoArgsConstructor
    @Getter
    @Setter
    public static class PhotoDto extends NamedDto {
        private List<DefectDto> defects;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class DefectDto {
        private String name;
        private List<CoordsDto> coords;

    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class CoordsDto {
        private Integer x;
        private Integer y;
    }
}
