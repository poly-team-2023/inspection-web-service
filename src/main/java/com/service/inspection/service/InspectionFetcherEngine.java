package com.service.inspection.service;

import com.service.inspection.entities.Inspection;
import com.service.inspection.entities.User;
import com.service.inspection.repositories.InspectionRepository;
import com.service.inspection.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class InspectionFetcherEngine {

    private final InspectionRepository inspectionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Inspection getInspectionWithSubEntities(Long id) {
        Inspection inspection = inspectionRepository.findInspectionById(id);
        Inspection secondFetch = inspection != null ? inspectionRepository.findInspectionByIdIs(id) : null;

        Optional.ofNullable(secondFetch).map(Inspection::getCategories)
                .ifPresent(categories -> categories.forEach(c -> Hibernate.initialize(c.getPhotos())));

        Optional.ofNullable(secondFetch).map(Inspection::getCompany).ifPresent(company -> {
            Hibernate.initialize(company.getFilesSro());
            Optional.ofNullable(company.getLicenses())
                    .ifPresent(licenses -> licenses.forEach(q -> Hibernate.initialize(q.getFiles())));
        });

        return secondFetch;
    }

    @Transactional(readOnly = true)
    public User getUserWithSubEntity(Long id) {
        User user = userRepository.findUserById(id);
        Optional.ofNullable(user).map(User::getEquipment)
                .ifPresent(equipments -> equipments
                        .forEach(equipment -> Hibernate.initialize(equipment.getFiles())));
        return user;
    }
}
