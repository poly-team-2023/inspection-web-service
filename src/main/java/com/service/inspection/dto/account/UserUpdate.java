package com.service.inspection.dto.account;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class UserUpdate {
    @NotBlank
    private String email;
    @NotBlank
    private String firstName;
    @NotBlank
    private String secondName;
    private String patronymic;
    private String number;
}
