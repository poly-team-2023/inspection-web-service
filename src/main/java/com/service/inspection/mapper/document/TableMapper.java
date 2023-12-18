package com.service.inspection.mapper.document;

import com.deepoove.poi.data.*;
import com.google.common.base.Strings;
import com.service.inspection.entities.Equipment;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.Optional;
import java.util.Set;

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
        ).right().width(17.01f, new double[] {1.64f, 7.75f, 3.63f, 3.99f}).create();

        Integer i = 1;
        for (Equipment equipment: equipmentSet) {
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
}
