package com.service.inspection.repositories;

import com.service.inspection.entities.Inspection;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {
    Page<Inspection> findByUsersId(Long userId, Pageable pageable);

    boolean existsByIdAndUsersId(long id, long userId);

    Inspection findByUsersIdAndId(Long userId, Long inspectionId);

    @EntityGraph(attributePaths = {"users.roles", "company", "employer"})
    Inspection findInspectionById(Long id);

    @EntityGraph(attributePaths = {"categories"})
    Inspection findInspectionByIdIs(Long id);
}
