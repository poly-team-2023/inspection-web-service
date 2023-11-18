package com.service.inspection.repositories;

import com.service.inspection.entities.Inspection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {
    Page<Inspection> findByUsersId(Long userId, Pageable pageable);

    Inspection findByUsersIdAndId(Long userId, Long inspectionId);
}
