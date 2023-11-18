package com.service.inspection.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.inspection.dto.auth.UserSignInDto;
import com.service.inspection.dto.auth.UserSignUpDto;
import com.service.inspection.entities.User;
import com.service.inspection.repositories.RoleRepository;
import com.service.inspection.repositories.UserRepository;
import com.service.inspection.service.AbstractTestContainerStartUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class AuthTest extends AbstractTestContainerStartUp {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users", "user_roles");
    }

    @Test
    void shouldUnauthorizedStatus_whenTryingGetAccessAnyEndPoint() throws Exception {
        mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/some")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void shouldAuth_whenUserExist() throws Exception {
        User user = new User();
        user.setFirstName("test");
        user.setSecondName("test");
        user.setPassword(passwordEncoder.encode("test-password"));
        user.setEmail("test@mail.com");
        userRepo.save(user);

        UserSignInDto userSignInDto = new UserSignInDto();
        userSignInDto.setEmail(user.getEmail());
        userSignInDto.setPassword("test-password");

        mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/auth/sign-in")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userSignInDto))
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void shouldNotAuth_withWrongPassword() throws Exception {
        User user = new User();
        user.setFirstName("test");
        user.setSecondName("test");
        user.setPassword(passwordEncoder.encode("test-password"));
        user.setEmail("test@mail.com");
        userRepo.save(user);

        UserSignInDto userSignInDto = new UserSignInDto();
        userSignInDto.setEmail(user.getEmail());
        userSignInDto.setPassword("test-password-wrong");

        mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/auth/sign-in")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userSignInDto))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void shouldSuccessfullySignUp_withGoodCred() throws Exception {
        UserSignUpDto userSignUpDto = new UserSignUpDto();
        userSignUpDto.setFirstName("test");
        userSignUpDto.setSecondName("test");
        userSignUpDto.setEmail("test@mail.com");
        userSignUpDto.setPassword("password");


        mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/auth/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userSignUpDto))
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        assertThat(userRepo.findAll()).hasSize(1);
    }
}
