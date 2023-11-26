package com.service.inspection.configs;

import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.ConfigureBuilder;
import com.deepoove.poi.data.Pictures;
import com.deepoove.poi.data.style.PictureStyle;
import com.service.inspection.entities.Photo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

    @Bean
    public Map<Long, Optional<Set<Photo.Defect>>> getInnerMapStorage() {
        return new ConcurrentHashMap<>(100);
    }

    @Bean
    public File getMainTemplate() {
        File file;
        try {
            file = ResourceUtils.getFile("classpath:test-template2.docx");
        } catch (FileNotFoundException f) {
            throw new RuntimeException(); // TODO
        }
        return file;
    }
}
