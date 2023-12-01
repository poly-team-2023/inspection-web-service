package com.service.inspection.mapper;


import java.text.MessageFormat;

import com.service.inspection.email.UserFeedbackRequestDto;
import com.service.inspection.entities.FeedbackRequest;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring",
        imports = {UserFeedbackRequestDto.class}
)
public abstract class EmailMapper {

    @Autowired
    private MessageFormat messageFormat;

    @Value("${spring.mail.defaultRecipient}")
    private String recipient;

    @Value("${spring.mail.username}")
    private String sender;


    public SimpleMailMessage mapToEmailMessage(UserFeedbackRequestDto requestDto) {
        if (requestDto == null) return null;

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(sender);
        simpleMailMessage.setSubject("Заявка на сотрудничество");
        simpleMailMessage.setTo(recipient);

        Object[] args = {
                requestDto.getFullName(), requestDto.getCompanyName(), requestDto.getEmail(),
                requestDto.getNumber(), requestDto.getDetails()
        };

        simpleMailMessage.setText(messageFormat.format(args));
        return simpleMailMessage;
    }

    public abstract FeedbackRequest mapToFeedbackRequest(UserFeedbackRequestDto userFeedbackRequestDto);
}
