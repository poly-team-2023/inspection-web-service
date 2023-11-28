package com.service.inspection.repositories;

import java.util.Optional;

import com.service.inspection.entities.Equipment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    Iterable<Equipment> findEquipmentByUserId(Long id);

    Optional<Equipment> findByUserIdAndId(long userId, long equipmentId);
}
