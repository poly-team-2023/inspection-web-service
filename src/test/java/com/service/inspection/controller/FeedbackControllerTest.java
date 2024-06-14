package com.service.inspection.controller;

import static com.service.inspection.controller.InspectionControllerTest.asJsonString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.service.inspection.configs.security.jwt.AuthTokenFilter;
import com.service.inspection.email.UserFeedbackRequestDto;
import com.service.inspection.entities.FeedbackRequest;
import com.service.inspection.mapper.EmailMapper;
import com.service.inspection.service.CommonService;
import com.service.inspection.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = FeedbackController.class,
        excludeFilters = @ComponentScan.Filter(classes = AuthTokenFilter.class, type = FilterType.ASSIGNABLE_TYPE))
@WithMockUser
class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;
    @MockBean
    private EmailMapper emailMapper;
    @MockBean
    private CommonService commonService;

    @Test
    void testFeedback() throws Exception {
        UserFeedbackRequestDto userFeedbackRequestDto = new UserFeedbackRequestDto();
        userFeedbackRequestDto.setEmail("test@test.com");
        userFeedbackRequestDto.setFullName("test");

        when(emailMapper.mapToEmailMessage(eq(userFeedbackRequestDto))).thenReturn(new SimpleMailMessage());
        when(emailMapper.mapToFeedbackRequest(eq(userFeedbackRequestDto))).thenReturn(new FeedbackRequest());

        mockMvc.perform(
                post("/api/v1/feedback")
                        .content(asJsonString(userFeedbackRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(emailService, times(1)).sendSimpleMail(any(SimpleMailMessage.class));
        verify(commonService, times(1)).saveFeedback(any(FeedbackRequest.class));
    }
}
