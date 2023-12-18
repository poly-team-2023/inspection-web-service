package com.service.inspection.controller;

import com.service.inspection.dto.document.PhotoDefectsDto;
import com.service.inspection.mapper.PhotoMapper;
import com.service.inspection.service.DataService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/data")
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
@RequiredArgsConstructor
@Slf4j
public class NNController {
    private final DataService dataService;
    private final PhotoMapper photoMapper;

    @PostMapping("/photos/{photoId}")
    @Operation(summary = "Добавить дефекты к фото",
            description = "Будет произведено, только если соответствующее фото было запрошено")
    public ResponseEntity<PhotoDefectsDto> getCategoryPhoto(
            @PathVariable @Min(1) Long photoId, Authentication authentication,
            @RequestBody PhotoDefectsDto photoDefectsDto

    ) {
        dataService.updatePhotoInfo(photoId, photoMapper.mapToPhotos(photoDefectsDto.getDefectsDto()));
        return ResponseEntity.ok(photoDefectsDto);
    }
}
