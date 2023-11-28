package com.service.inspection.dto.auth;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Validated
public class UserSignInDto {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
