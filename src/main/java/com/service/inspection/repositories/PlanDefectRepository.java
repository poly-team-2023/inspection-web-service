package com.service.inspection.repositories;

import java.util.UUID;

import com.service.inspection.entities.PlanDefect;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanDefectRepository extends JpaRepository<PlanDefect, UUID> {
}
