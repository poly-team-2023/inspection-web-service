package com.service.inspection.mapper;


import com.service.inspection.dto.employer.EmployerDto;
import com.service.inspection.email.UserFeedbackRequestDto;
import com.service.inspection.entities.Employer;
import com.service.inspection.entities.FeedbackRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.MessageFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmailMapperImpl.class})
class EmailMapperTest {

    @Autowired
    private EmailMapper emailMapper;

    @MockBean
    private MessageFormat messageFormat;

    @Test
    void whenMapToFeedbackRequest_givenUserFeedbackRequestDto_shouldReturnFeedbackRequest() {
        // Arrange
        UserFeedbackRequestDto userFeedbackRequestDto = new UserFeedbackRequestDto();
        userFeedbackRequestDto.setEmail("test@example.com");
        userFeedbackRequestDto.setFullName("John Doe");
        userFeedbackRequestDto.setNumber("1234567890");
        userFeedbackRequestDto.setCompanyName("Test Company");
        userFeedbackRequestDto.setDetails("Need more information about your services.");

        // Act
        FeedbackRequest result = emailMapper.mapToFeedbackRequest(userFeedbackRequestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getFullName()).isEqualTo("John Doe");
        assertThat(result.getNumber()).isEqualTo("1234567890");
        assertThat(result.getCompanyName()).isEqualTo("Test Company");
        assertThat(result.getDetails()).isEqualTo("Need more information about your services.");
    }

    @Test
    void whenMapToFeedbackRequest_givenNullUserFeedbackRequestDto_shouldReturnNull() {
        // Act
        FeedbackRequest result = emailMapper.mapToFeedbackRequest(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void whenMapToEmailMessage_givenRequestDto_shouldReturnSimpleMailMessage() {
        UserFeedbackRequestDto requestDto = new UserFeedbackRequestDto();
        requestDto.setFullName("Jane Doe");
        requestDto.setCompanyName("Doe Enterprises");
        requestDto.setEmail("jane.doe@example.com");
        requestDto.setNumber("0987654321");
        requestDto.setDetails("Interested in partnership.");


        MessageFormat messageFormat = new MessageFormat("Name: {0}\nCompany: {1}\nEmail: {2}\nPhone: {3}\nDetails: {4}");
        Object[] args = {requestDto.getFullName(), requestDto.getCompanyName(), requestDto.getEmail(), requestDto.getNumber(), requestDto.getDetails()};

        when(this.messageFormat.format(any())).thenReturn(messageFormat.format(args));

        SimpleMailMessage result = emailMapper.mapToEmailMessage(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getFrom()).isEqualTo("${spring.mail.username}");
        assertThat(result.getTo()).containsExactly("${spring.mail.defaultRecipient}");
        assertThat(result.getSubject()).isEqualTo("Заявка на сотрудничество");
        assertThat(result.getText()).isEqualTo(messageFormat.format(args));
    }

    @Test
    void whenMapToEmailMessage_givenNullRequestDto_shouldReturnNull() {
        // Act
        SimpleMailMessage result = emailMapper.mapToEmailMessage(null);

        // Assert
        assertThat(result).isNull();
    }
}
