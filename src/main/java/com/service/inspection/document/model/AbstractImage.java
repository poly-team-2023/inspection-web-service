package com.service.inspection.document.model;

import com.deepoove.poi.data.PictureRenderData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractImage {
    private String imageTitle;
    private PictureRenderData image;
}
