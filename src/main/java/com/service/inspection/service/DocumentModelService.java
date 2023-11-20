package com.service.inspection.service;

import com.service.inspection.document.DocumentModel;
import com.service.inspection.entities.Photo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentModelService implements DocumentService {

    private final StorageService storageService;

    public void fillDefectsPhotos(DocumentModel documentModel, Collection<Photo>... photos) {

//        List<ImageModel> documentPhotoModel = new LinkedList<>();
//
//        for (Collection<Photo> photoCollection: photos) {
//            for (Photo photo: photoCollection) {
//                // TODO make async
//                if (photo.getFileUuid() != null && photo.getDefectsCoords() != null) {
//                    ImageModel photoModel = new ImageModel();
//
//                    StorageService.BytesWithContentType file =
//                            storageService.getFile(BucketName.CATEGORY_PHOTOS, photo.getFileUuid().toString());
//
//                    photoModel.setDefects(null);
//                    photoModel.setBytes(file.getBytes());
//                    documentPhotoModel.add(photoModel);
//                }
//            }
//        }
    }
}
