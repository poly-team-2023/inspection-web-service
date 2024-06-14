package com.service.inspection.repositories;

import com.service.inspection.entities.PhotoPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoPlanRepository extends JpaRepository<PhotoPlan, Long> {
}
