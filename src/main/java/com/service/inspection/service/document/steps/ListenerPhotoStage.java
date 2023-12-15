package com.service.inspection.service.document.steps;

import com.service.inspection.entities.Photo;
import com.service.inspection.repositories.PhotoRepository;
import com.service.inspection.service.document.ProcessingImageDto;
import jakarta.mail.Multipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Slf4j
@Order(2)
@Service
@RequiredArgsConstructor
public class ListenerPhotoStage extends AbstractImageProcessingStep {
    private final PhotoRepository photoRepository;
    private final Map<Long, BlockingQueue<Set<Photo.Defect>>> photosUpdatingStorage;
    private final WebClient imageProcessingClient;

    @Override
    public void executeProcess(ProcessingImageDto imageModel) {
        if (imageModel.getId() == null) {
            return;
        }

        Set<Photo.Defect> defects = photoRepository.findById(imageModel.getId()).orElse(null)
                .getDefectsCoords();

        if (defects == null) {
            BlockingQueue<Set<Photo.Defect>> result = new ArrayBlockingQueue<>(1);
            photosUpdatingStorage.putIfAbsent(imageModel.getId(), result);

            sendRequestToAnalyze(imageModel.getPhotoBytes(), imageModel.getId(), "facade");

            try {
                log.debug("Block for waiting photo {} {}", imageModel.getId(), Thread.currentThread().getName());
                defects = result.poll(20, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.debug("Exception {} while waiting defects {}", e.getMessage(), Thread.currentThread().getName());
                return;
            }
            log.debug("Unblock after waiting photo {} {}", imageModel.getId(), Thread.currentThread().getName());

            photosUpdatingStorage.remove(imageModel.getId());
        }
        log.info("For photo {} find {}", imageModel.getId(), Optional.ofNullable(imageModel.getDefects()).map(Set::size).orElse(0));
        imageModel.setDefects(defects);
    }


    private void sendRequestToAnalyze(byte[] photoBytes, Long id, String category) {
        Resource r = new ByteArrayResource(photoBytes);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("nn_mode", category);
        builder.part("image", r).filename("test.png").contentType(MediaType.IMAGE_PNG);

        imageProcessingClient.mutate().build().post()
                .uri(uriBuilder -> uriBuilder.path("image").queryParam("id", id).build())
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve().toBodilessEntity()
                .retryWhen(Retry.fixedDelay(5,
                        Duration.of(5, TimeUnit.SECONDS.toChronoUnit())
                )).filter(t -> t.getStatusCode().isError()).then();
    }
}
