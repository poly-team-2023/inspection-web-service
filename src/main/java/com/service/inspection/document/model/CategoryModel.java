package com.service.inspection.document.model;

import com.deepoove.poi.data.NumberingRenderData;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class CategoryModel {
    private Long id;
    private Long categoryNum = 0L;
    private String name;

    // Раздел который в будущем надо сделать. Последняя таблица со сводной таблицей дефектов и того,
    // какие дефекты на них были выявлены.
    private Map<String, Pair<String, List<Long>>> defectsWithPhotos;

    // Раздел в котором делается вывод по категориям.
    private String recommendation;
    private String estimation;

    private Set<String> defects;

    private List<ImageModelWithDefects> photos;
}
