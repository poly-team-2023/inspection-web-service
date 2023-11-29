package com.service.inspection.email;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFeedbackRequestDto {
    @NotBlank
    private String fullName;
    @NotBlank
    private String email;
    private String number;
    private String companyName;
    private String details;
}
