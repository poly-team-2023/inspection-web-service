package com.service.inspection.service;

import com.service.inspection.document.DocumentModel;
import com.service.inspection.entities.Category;
import com.service.inspection.entities.Inspection;
import com.service.inspection.entities.Photo;
import com.service.inspection.repositories.InspectionRepository;
import com.service.inspection.service.document.ImageProcessingFacade;
import com.service.inspection.service.document.TestImageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentModelService {

    private final StorageService storageService;
    private final ImageProcessingFacade imageProcessingFacade;
    private final InspectionRepository inspectionRepository;

    @Async
    public void processAllPhotosAsync(Set<Photo> photos) {
        log.info("Start all photos processing" + Thread.currentThread().getName());

        List<CompletableFuture<TestImageModel>> futures = new ArrayList<>();
        for (Photo photo: photos) {
            TestImageModel testImageModel = new TestImageModel();
            testImageModel.setPhoto(photo);
            futures.add(imageProcessingFacade.processPhoto(testImageModel));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("End document processing" + Thread.currentThread().getName());

//        try (XWPFTemplate template = XWPFTemplate.compile(file).render(
//                documentMapper.mapToDocumentModel(inspectionRepository.findById(inspectionId).orElse(null)))) {
//            template.writeToFile("compile-result.docx");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }


    public static <T> CompletableFuture<List<T>> all(List<CompletableFuture<T>> futures) {
        CompletableFuture[] cfs = futures.toArray(new CompletableFuture[futures.size()]);

        return CompletableFuture.allOf(cfs)
                .thenApply(ignored -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
                );
    }
}
