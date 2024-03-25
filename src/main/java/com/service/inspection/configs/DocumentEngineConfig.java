package com.service.inspection.configs;

import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.ConfigureBuilder;
import com.deepoove.poi.data.style.PictureStyle;
import com.deepoove.poi.plugin.toc.TOCRenderPolicy;
import com.service.inspection.entities.Photo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@RequiredArgsConstructor
public class DocumentEngineConfig {

    private final ResourceLoader resourceLoader;

    @Bean
    public Configure builderCreator() {
        ConfigureBuilder builder = Configure.builder();
        builder.useSpringEL(false);
        builder.bind("TOC", new TOCRenderPolicy());
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
    public Map<Long, BlockingQueue<Set<Photo.Defect>>> getInnerMapStorage() {
        return new ConcurrentHashMap<>(100);
    }

    @Bean(name="mainTemplatePath")
    public String getMainTemplatePath() {
        return "classpath:test-template2.docx";
    }
}
