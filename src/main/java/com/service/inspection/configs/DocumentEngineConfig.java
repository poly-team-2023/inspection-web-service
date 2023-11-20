package com.service.inspection.configs;

import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.ConfigureBuilder;
import com.deepoove.poi.data.Pictures;
import com.deepoove.poi.data.style.PictureStyle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentEngineConfig {

    @Bean
    public Configure builderCreator() {
        ConfigureBuilder builder = Configure.builder();
        builder.useSpringEL(true);
        return builder.build();
    }

    @Bean
    public PictureStyle getCategoriesPhotosStyle() {
        PictureStyle style = new PictureStyle();
        style.setWidth(100);
        style.setHeight(100);
        return style;
    }


}
