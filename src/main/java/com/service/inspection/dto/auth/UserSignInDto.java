package com.service.inspection.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class UserSignInDto {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
