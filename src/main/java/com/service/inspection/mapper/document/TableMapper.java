package com.service.inspection.mapper.document;

import com.deepoove.poi.data.*;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.service.inspection.document.model.CategoryDefectsModel;
import com.service.inspection.document.model.CategoryModel;
import com.service.inspection.entities.Equipment;
import com.service.inspection.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"
)
@Slf4j
public abstract class TableMapper {

    @Autowired
    private CommonUtils utils;

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
        MergeCellRule.MergeCellRuleBuilder ruleBuilder = MergeCellRule.builder();
        TableRenderData tableRenderData = Tables.of(Rows.of("№№ п/п", "Характеристика дефекта",
                        "Место расположения дефекта", "Возможный способ устранения дефекта", "№ фото (см. Приложение В)")
                .center().textFontSize(12).textBold().textFontFamily("Times New Roman").create()).left()
                .width(26.81f, new double[]{2.57f, 7.42f, 4.1f, 7.97f, 4.74f}).create();

        int currentRowCount = 1;
        for (CategoryModel category : categories) {
            tableRenderData.addRow(Rows.of(category.getCategoryNum() + ". " + category.getName(), null, null, null,
                    null).center().textFontSize(12).textBold().textFontFamily("Times New Roman").create());

            ruleBuilder.map(MergeCellRule.Grid.of(currentRowCount, 0), MergeCellRule.Grid.of(currentRowCount, 3));

            if (category.getDefectsWithPhotos() == null) continue;

            int currentLocalCounter = 1;
            for (Map.Entry<String, CategoryDefectsModel> entry: category.getDefectsWithPhotos().entrySet()) {
                tableRenderData.addRow(Rows.of(category.getCategoryNum() + "." + currentLocalCounter++,
                                        utils.toHumanReadable(entry.getKey()), null, entry.getValue().getRecommendation(),
                        "Фото №№" + Joiner.on(", №").skipNulls().join(entry.getValue().getPhotoNums()))
                        .textFontSize(10).textFontFamily("Times New Roman").create());
            }

            currentRowCount += category.getDefectsWithPhotos().size() + 1;
        }

        tableRenderData.setMergeRule(ruleBuilder.build());
        return tableRenderData;
    }
}
