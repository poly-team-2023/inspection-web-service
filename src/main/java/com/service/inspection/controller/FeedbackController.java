package com.service.inspection.controller;


import com.service.inspection.email.UserFeedbackRequestDto;
import com.service.inspection.mapper.EmailMapper;
import com.service.inspection.service.CommonService;
import com.service.inspection.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/feedback")
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
@RequiredArgsConstructor
public class FeedbackController {

    private final EmailService emailService;
    private final EmailMapper emailMapper;
    private final CommonService commonService;

    @PostMapping
    public ResponseEntity<Void> notifyAdmin(@RequestBody UserFeedbackRequestDto feedbackDto) {
        emailService.sendSimpleMail(emailMapper.mapToEmailMessage(feedbackDto));
        commonService.saveFeedback(emailMapper.mapToFeedbackRequest(feedbackDto));
        return ResponseEntity.ok().build();
    }
}
