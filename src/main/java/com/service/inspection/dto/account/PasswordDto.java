package com.service.inspection.dto.account;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordDto {
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword; // TODO написать regex при согласовании ограничения пароля
}
