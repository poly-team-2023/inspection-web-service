package com.service.inspection.controller;

import com.service.inspection.dto.document.PhotoDefectsDto;
import com.service.inspection.mapper.PhotoMapper;
import com.service.inspection.service.DataService;
import com.service.inspection.service.GptTesterService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/data")
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
@RequiredArgsConstructor
@Slf4j
public class NNController {
    private final DataService dataService;
    private final PhotoMapper photoMapper;
    private final GptTesterService testService;

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

    @GetMapping("/gpt/test")
    public void testToDeleteLater() {
        testService.sendTestGptRequest();
    }
}
