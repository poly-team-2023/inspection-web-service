package com.service.inspection.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessagePOJO {
    private String fullName;
    private String email;
    private String number;
    private String companyName;
    private String details;
}
