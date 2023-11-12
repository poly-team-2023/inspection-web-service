package com.service.inspection.controller;

import com.service.inspection.dto.inspection.InspectionWithIdOnly;
import com.service.inspection.dto.inspection.InspectionWithName;
import com.service.inspection.entities.Inspection;
import com.service.inspection.mapper.InspectionMapper;
import com.service.inspection.service.InspectionService;
import com.service.inspection.service.security.UserDetailsImpl;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inspections")
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;
    private final InspectionMapper inspectionMapper;

    // TODO заменить обращение к бд для поиска пользователя с email на
    //  id для более быстрого поиска

    @PostMapping
    public ResponseEntity<InspectionWithIdOnly> createInspection(Authentication authentication) {
        String username = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
        Long inspectionId = inspectionService.createInspection(username);

        InspectionWithIdOnly inspectionOnlyIdDto = new InspectionWithIdOnly();
        inspectionOnlyIdDto.setId(inspectionId);

        return ResponseEntity.ok().body(inspectionOnlyIdDto);
    }

    @GetMapping
    public ResponseEntity<Page<InspectionWithName>> getAllInspections(
            @RequestParam(defaultValue = "0", required = false) Integer pageNum,
            @RequestParam(defaultValue = "10", required = false) Integer pageSize,
            Authentication authentication
    ) {
        String username = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
        Page<Inspection> page = inspectionService.getUserInspection(username, pageSize, pageNum);

        return ResponseEntity.ok().body(
                page.map(inspectionMapper::mapToInspectionWithName)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInspection(@PathVariable @Min(1) Integer id) {
        inspectionService.deleteInspection(id);
        return ResponseEntity.ok().build();
    }
}
