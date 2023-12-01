package com.service.inspection.service;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Async
    public void sendSimpleMail(SimpleMailMessage mailMessage) {
        try {
            javaMailSender.send(mailMessage); // TODO добавить retry + recover
        } catch (MailException mailException) {
            log.error(mailException.getMessage());
        }
    }
}
