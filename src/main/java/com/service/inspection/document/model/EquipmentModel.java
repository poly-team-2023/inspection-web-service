package com.service.inspection.document.model;

import com.deepoove.poi.data.TableRenderData;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class EquipmentModel {
    private TableRenderData table;
    private List<ImageModel> scans = Collections.synchronizedList(new ArrayList<>());
}
