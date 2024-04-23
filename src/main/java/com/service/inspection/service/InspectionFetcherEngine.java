package com.service.inspection.service;

import com.service.inspection.entities.Inspection;
import com.service.inspection.repositories.InspectionRepository;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class InspectionFetcherEngine {


    private final InspectionRepository inspectionRepository;

    @Transactional(readOnly = true)
    public Inspection getInspectionWithSubEntities(Long id) {
        Inspection inspection = inspectionRepository.findInspectionById(id);

        Inspection secondFetch = inspection != null ? inspectionRepository.findInspectionByIdIs(id) : null;

        Optional.ofNullable(secondFetch).map(Inspection::getCategories)
                .ifPresent(categories -> categories.forEach(c -> Hibernate.initialize(c.getPhotos())));

        return secondFetch;
    }
}
