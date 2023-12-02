package com.service.inspection.service;

import com.service.inspection.entities.FeedbackRequest;
import com.service.inspection.repositories.FeedbackRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonService {
    private final FeedbackRepository feedbackRepository;

    public void saveFeedback(FeedbackRequest feedbackRequest) {
        feedbackRepository.save(feedbackRequest);
    }
}
