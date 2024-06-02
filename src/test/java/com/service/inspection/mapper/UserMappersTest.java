package com.service.inspection.mapper;


import com.service.inspection.dto.account.UserUpdate;
import com.service.inspection.dto.account.UserWithCompanyDto;
import com.service.inspection.dto.auth.UserSignUpDto;
import com.service.inspection.entities.Company;
import com.service.inspection.entities.Role;
import com.service.inspection.entities.User;
import com.service.inspection.entities.enums.ERole;
import com.service.inspection.repositories.CategoryRepository;
import com.service.inspection.repositories.CompanyRepository;
import com.service.inspection.repositories.EmployerRepository;
import com.service.inspection.repositories.PlanRepository;
import com.service.inspection.service.security.UserDetailsImpl;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserMapperImpl.class, EntityFactory.class, BCryptPasswordEncoder.class})
class UserMappersTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private CompanyRepository companyRepository;
    @MockBean
    private EmployerRepository employerRepository;
    @MockBean
    private PlanRepository planRepository;
    @MockBean
    private CategoryRepository categoryRepository;


    @Test
    void testUserMapperMapToUser() {
        UserSignUpDto userSignUpDto = new UserSignUpDto();
        userSignUpDto.setEmail("test@test.com");
        userSignUpDto.setPassword("123456");
        userSignUpDto.setSecondName("SecondName");
        userSignUpDto.setPatronymic("Patronymic");
        userSignUpDto.setFirstName("FirstName");

        User user = userMapper.mapToUser(userSignUpDto);
        assertThat(user.getEmail()).isEqualTo(userSignUpDto.getEmail());
        assertThat(passwordEncoder.matches(userSignUpDto.getPassword(), user.getPassword())).isTrue();
        assertThat(user.getFirstName()).isEqualTo(userSignUpDto.getFirstName());
        assertThat(user.getSecondName()).isEqualTo(userSignUpDto.getSecondName());
        assertThat(user.getEmail()).isEqualTo(userSignUpDto.getEmail());
        assertThat(user.getRoles()).extracting("id").containsOnly(1L);
    }

    @Test
    void testUserMapperMapToUserWithCompany() {
        Company company = new Company();
        company.setName("CompanyName");
        company.setId(1L);

        Company company2 = new Company();
        company2.setName("CompanyName2");
        company2.setId(2L);

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("123456");
        user.setFirstName("FirstName");
        user.setSecondName("SecondName");
        user.setNumber("123456789");
        user.setPatronymic("Patronymic");
        user.setCompanies(Set.of(company, company2));

        UserWithCompanyDto userWithCompanyDto = userMapper.mapToUserWithCompany(user);
        assertThat(userWithCompanyDto.getEmail()).isEqualTo(user.getEmail());
        assertThat(userWithCompanyDto.getNumber()).isEqualTo(user.getNumber());
        assertThat(userWithCompanyDto.getPatronymic()).isEqualTo(user.getPatronymic());
        assertThat(userWithCompanyDto.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(userWithCompanyDto.getSecondName()).isEqualTo(user.getSecondName());
        assertThat(userWithCompanyDto.getCompanies())
                .extracting("id", "name")
                .containsExactlyInAnyOrder(
                        Tuple.tuple(company.getId(), company.getName()),
                        Tuple.tuple(company2.getId(), company2.getName())
                );
    }

    @Test
    void testUserMapperMapToUserDetailsImpl() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("123456");
        user.setFirstName("FirstName");
        user.setSecondName("SecondName");
        user.setNumber("123456789");
        user.setPatronymic("Patronymic");

        Role role = new Role();
        role.setName(ERole.ROLE_USER);
        role.setId(1L);

        user.setRoles(List.of(role));

        UserDetailsImpl userDetails = userMapper.mapToUserDetailsImpl(user);
        assertThat(userDetails.getUser()).isEqualTo(user);
        assertThat(userDetails.getEmail()).isEqualTo(user.getEmail());
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
        assertThat(userDetails.getUsername()).isEqualTo(user.getEmail());
        assertThat(userDetails.getAuthorities()).map(GrantedAuthority::getAuthority).containsOnly(ERole.ROLE_USER.name());
    }

    @Test
    void testUserMapperMapToUserWithRoles() {
        UserUpdate userUpdate = new UserUpdate();
        userUpdate.setPatronymic("test@test.com");
        userUpdate.setEmail("new@test.com");
        userUpdate.setFirstName("FirstName10");
        userUpdate.setSecondName("SecondName10");
        userUpdate.setNumber("12345678910");

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("123456");
        user.setFirstName("FirstName");
        user.setSecondName("SecondName");
        user.setNumber("123456789");
        user.setPatronymic("Patronymic");

        userMapper.mapToUpdateUser(user, userUpdate);

        assertThat(user.getEmail()).isEqualTo(userUpdate.getEmail());
        assertThat(user.getFirstName()).isEqualTo(userUpdate.getFirstName());
        assertThat(user.getSecondName()).isEqualTo(userUpdate.getSecondName());
        assertThat(user.getNumber()).isEqualTo(userUpdate.getNumber());
        assertThat(user.getPatronymic()).isEqualTo(userUpdate.getPatronymic());
    }
}
