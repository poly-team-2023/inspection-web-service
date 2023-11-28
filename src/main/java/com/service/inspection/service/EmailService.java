package com.service.inspection.service;

import com.service.inspection.email.EmailMessagePOJO;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
@Service
public class EmailService {


    private final JavaMailSender javaMailSender;

//    @Value("${spring.mail.username}")
    private String sender;

    public String sendSimpleMail(EmailMessagePOJO details) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("checkpoint.k.bot@gmail.com");
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());
            javaMailSender.send(mailMessage);
            return "Mail Sent Successfully...";
        }
        // Catch block to handle the exceptions
        catch (Exception e) {
            return "Error while Sending Mail";
        }
    }
}
