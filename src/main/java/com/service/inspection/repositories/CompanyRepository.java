package com.service.inspection.repositories;

import java.util.Optional;

import com.service.inspection.entities.Company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByUserIdAndId(long userId, long companyId);
}
