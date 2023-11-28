package com.service.inspection.dto.account;

import com.service.inspection.dto.NamedDto;
import lombok.Data;

import java.util.Set;

@Data
public class UserWithCompanyDto {
    private String email;
    private String firstName;
    private String secondName;
    private String patronymic;
    private String number;
    private Set<NamedDto> companies;
}
