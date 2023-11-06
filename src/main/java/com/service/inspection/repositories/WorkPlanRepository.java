package com.service.inspection.repositories;

import com.service.inspection.entities.Inspection;
import jakarta.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

@Resource
public interface WorkPlanRepository extends JpaRepository<Inspection.WorkPlan, Long> {
}
