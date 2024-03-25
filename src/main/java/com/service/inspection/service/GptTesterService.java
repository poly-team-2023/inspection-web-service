package com.service.inspection.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.inspection.dto.document.GptReceiverDto;
import com.service.inspection.dto.document.GptSenderDto;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GptTesterService {

    private RabbitTemplate rabbitTemplate;
    private ObjectMapper objectMapper;

    public void sendTestGptRequest() {

        GptSenderDto.GptDefectDto gptDefectDto = new GptSenderDto.GptDefectDto("биопоражение", 25L);
        GptSenderDto.GptDefectDto gptDefectDto1 = new GptSenderDto.GptDefectDto("фиг знает что", 2L);
        GptSenderDto.GptDefectDto gptDefectDto2 = new GptSenderDto.GptDefectDto("душный скуф", 10L);

        GptSenderDto.GptCategoryDto gptCategoryDto = new GptSenderDto.GptCategoryDto();
        gptCategoryDto.setId(1L);
        gptCategoryDto.setName("хуяся");
        gptCategoryDto.setDefects(List.of(gptDefectDto, gptDefectDto1, gptDefectDto2));

        GptSenderDto senderDto = new GptSenderDto();
        senderDto.setCategories(List.of(gptCategoryDto));

        byte[] b = null;
        try {
            b = objectMapper.writeValueAsBytes(senderDto);
        } catch (Exception e) {
            return;
        }

        Message message = MessageBuilder.withBody(b)
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setCorrelationId(UUID.randomUUID().toString()).build();

        Message m = rabbitTemplate.sendAndReceive("nlm.task", message);

        try {
            GptReceiverDto receiverDto = objectMapper.readValue(m.getBody(),  GptReceiverDto.class);
            System.out.println(receiverDto);
        } catch (Exception e) {
            return;
        }

    }
}
