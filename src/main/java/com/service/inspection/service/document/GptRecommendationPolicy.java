package com.service.inspection.service.document;

import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.data.TableRenderData;
import com.deepoove.poi.policy.DynamicTableRenderPolicy;
import com.deepoove.poi.policy.TableRenderPolicy;
import com.service.inspection.document.model.CategoryModel;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import java.util.List;

public class GptRecommendationPolicy extends DynamicTableRenderPolicy {

    @Override
    public void render(XWPFTable xwpfTable, Object o) throws Exception {
        int currentRow = 0;

    }

}
