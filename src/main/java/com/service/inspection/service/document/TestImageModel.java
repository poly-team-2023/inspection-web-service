package com.service.inspection.service.document;

import com.service.inspection.entities.Photo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestImageModel {
    private byte[] photoBytes;


    private Photo photo;
}
