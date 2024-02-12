package com.service.inspection.mapper.document;

import com.deepoove.poi.data.*;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.service.inspection.document.model.CategoryDefectsModel;
import com.service.inspection.document.model.CategoryModel;
import com.service.inspection.document.model.DefectModel;
import com.service.inspection.document.model.ImageModelWithDefects;
import com.service.inspection.entities.Equipment;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"
)
@Slf4j
public abstract class TableMapper {

    private RowRenderData mapToRowRenderData(Equipment equipment, Integer num) {
        return Rows.of(
                this.mapToCellRenderData(num.toString()),
                this.mapToCellRenderData(equipment.getName(), ParagraphAlignment.LEFT),
                this.mapToCellRenderData(Optional.ofNullable(
                        Strings.emptyToNull(equipment.getSerialNumber())).orElse("-")),
                this.mapToCellRenderData(Optional.ofNullable(
                        Strings.emptyToNull(equipment.getVerificationNumber())).orElse("-"))
        ).create();
    }


    public TableRenderData mapToTableRenderData(Set<Equipment> equipmentSet) {
        TableRenderData tableRenderData = Tables.of(
                Rows.of("№ п/п", "Наименование СИ и ИО", "Зав. №", "Сведения о поверке/калибровке")
                        .horizontalCenter().verticalCenter().create()
        ).right().width(17.01f, new double[]{1.64f, 7.75f, 3.63f, 3.99f}).create();

        Integer i = 1;
        for (Equipment equipment : equipmentSet) {
            tableRenderData.addRow(mapToRowRenderData(equipment, i++));
        }

        return tableRenderData;
    }


    private CellRenderData mapToCellRenderData(String text, ParagraphAlignment alignment) {
        CellRenderData cellRenderData = Cells.of(text).horizontalCenter().verticalCenter().create();

        cellRenderData.getCellStyle().getDefaultParagraphStyle().setAlign(alignment);

        return cellRenderData;
    }

    private CellRenderData mapToCellRenderData(String text) {
        return this.mapToCellRenderData(text, ParagraphAlignment.CENTER);
    }

    public TableRenderData createSumDefectsTable(List<CategoryModel> categories) {

        TableRenderData tableRenderData = Tables.of(Rows.of("1", "2", "3", "4").create()).right()
                .width(17.01f, new double[]{1.64f, 7.75f, 3.63f, 3.99f}).create();

        int currentCategoryNum = 1;
        for (CategoryModel category : categories) {
            tableRenderData.addRow(Rows.of(null, null, currentCategoryNum + ". " + category.getName(), null).create());
            if (category.getDefectsWithPhotos() == null) continue;
            category.getDefectsWithPhotos().forEach((def, photos) -> {
                tableRenderData.addRow(Rows.of(def, null, photos.getRecommendation(),
                        Joiner.on(", ").join(photos.getPhotoNums())).create());
            });
            currentCategoryNum += 1;
        }
        return tableRenderData;
    }
}
