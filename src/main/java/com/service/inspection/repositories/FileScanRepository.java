package com.service.inspection.repositories;

import com.service.inspection.entities.Company;
import com.service.inspection.entities.FileScan;
import com.service.inspection.entities.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileScanRepository extends JpaRepository<FileScan, Long> {

}
