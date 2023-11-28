package com.service.inspection.controller;


import com.service.inspection.email.EmailMessagePOJO;
import com.service.inspection.service.EmailService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notify")
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
@RequiredArgsConstructor
public class StartController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<Void> notifyAdmin(@RequestBody EmailMessagePOJO emailMessagePOJO){
        emailService.sendSimpleMail(emailMessagePOJO);
        return ResponseEntity.ok().build();
    }
}
