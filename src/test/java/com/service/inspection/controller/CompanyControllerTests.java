package com.service.inspection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.inspection.dto.auth.UserSignInDto;
import com.service.inspection.dto.company.CompanyDto;
import com.service.inspection.entities.User;
import com.service.inspection.jwt.JwtUtils;
import com.service.inspection.repositories.RoleRepository;
import com.service.inspection.repositories.UserRepository;
import com.service.inspection.service.AbstractTestContainerStartUp;
import com.service.inspection.service.AuthService;
import com.service.inspection.service.security.UserDetailsImpl;
import com.service.inspection.utils.ControllerUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.security.Principal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class CompanyControllerTests extends AbstractTestContainerStartUp {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtils jwtUtils;

//    @MockBean
//    private ControllerUtils controllerUtils;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users", "user_roles");
    }

    @Test
    void test() throws Exception {
        User user = new User();
        user.setFirstName("test");
        user.setSecondName("test");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEmail("test@example.com");
        userRepo.save(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken("test@example.com", "password");
        authentication = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mock JWT token generation
        when(jwtUtils.generateJwtToken(any(Authentication.class))).thenReturn("mockedJWTToken");

//         Mock cookie creation
//        when(controllerUtils.createJwtCookie(any())).thenReturn(null );

        UserSignInDto userSignInDto = new UserSignInDto();
        userSignInDto.setEmail(user.getEmail());
        userSignInDto.setPassword("test-password");

        CompanyDto companyDto = new CompanyDto();
        companyDto.setName("estripper");
        companyDto.setCity("saint-pi");
        companyDto.setLegalAddress("turku");

        Authentication finalAuthentication = authentication;
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/?comp_id=12")
                        .content(objectMapper.writeValueAsString(companyDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(request -> {
                            request.setUserPrincipal(finalAuthentication);
                            return request;
                        }))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
