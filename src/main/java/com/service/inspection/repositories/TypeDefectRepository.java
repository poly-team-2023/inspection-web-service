package com.service.inspection.repositories;

import java.util.UUID;

import com.service.inspection.entities.DefectType;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeDefectRepository extends JpaRepository<DefectType, UUID> {
}
