package com.service.inspection.repositories;

import com.service.inspection.entities.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {

    Iterable<License> findLicensesByCompanyId(long id);
}
