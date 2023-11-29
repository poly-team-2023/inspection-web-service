package com.service.inspection.controller;

import com.service.inspection.dto.document.PhotoDefectsDto;
import com.service.inspection.entities.Photo;
import com.service.inspection.mapper.PhotoMapper;
import com.service.inspection.service.DataService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/data")
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
@RequiredArgsConstructor
public class NNController {
    private final DataService dataService;
    private final PhotoMapper photoMapper;

    @PostMapping("/photos/{photoId}")
    @Operation(summary = "Добавить дефекты к фото", hidden = true)
    public ResponseEntity<Resource> getCategoryPhoto(
            @PathVariable @Min(1) Long photoId, Authentication authentication,
            @RequestBody PhotoDefectsDto photoDefectsDto

    ) {
        Set<Photo.Defect> photoDefects = photoMapper.mapToPhotos(photoDefectsDto.getDefectsDto());
        dataService.updatePhotoInfo(photoId, photoDefects);
        return ResponseEntity.ok().build();
    }
}
