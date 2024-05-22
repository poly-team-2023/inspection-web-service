package com.service.inspection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.inspection.dto.inspection.PhotoCreateDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class Conv implements Converter<String, PhotoCreateDto> {

    @Autowired
    public ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public PhotoCreateDto convert(String source) {
        return objectMapper.readValue(source, PhotoCreateDto.class);
    }
}
