package com.service.inspection.repositories;

import com.service.inspection.entities.FeedbackRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackRequest, Long> {
}
