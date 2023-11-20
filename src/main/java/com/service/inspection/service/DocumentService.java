package com.service.inspection.service;

import com.service.inspection.document.DocumentModel;
import com.service.inspection.entities.Photo;

import java.util.Collection;

public interface DocumentService {
    void fillDefectsPhotos(DocumentModel documentModel, Collection<Photo>... photos);
}
