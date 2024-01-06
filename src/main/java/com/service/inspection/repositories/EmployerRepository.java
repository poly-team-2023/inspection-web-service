package com.service.inspection.repositories;

import com.service.inspection.entities.Employer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long> {

    List<Employer> findAllByCompanyId(long companyId);

    Optional<Employer> findById(long employerId);
}
