package com.service.inspection.controller;

import static com.service.inspection.controller.InspectionControllerTest.asJsonString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.service.inspection.configs.security.jwt.AuthTokenFilter;
import com.service.inspection.configs.security.jwt.JwtUtils;
import com.service.inspection.dto.NamedDto;
import com.service.inspection.dto.account.PasswordDto;
import com.service.inspection.dto.account.UserUpdate;
import com.service.inspection.dto.account.UserWithCompanyDto;
import com.service.inspection.entities.User;
import com.service.inspection.mapper.UserMapper;
import com.service.inspection.service.StorageService;
import com.service.inspection.service.UserAccountService;
import com.service.inspection.utils.ControllerUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@WebMvcTest(controllers = UserAccountController.class,
        excludeFilters = @ComponentScan.Filter(classes = AuthTokenFilter.class, type = FilterType.ASSIGNABLE_TYPE))
@WithMockUser
class UserAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAccountService userAccountService;
    @MockBean
    private ControllerUtils controllerUtils;
    @MockBean
    private JwtUtils jwtUtils;
    @MockBean
    private UserMapper userMapper;

    @BeforeEach
    public void setUp() {
        when(controllerUtils.getUserId(any())).thenReturn(1L);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        when(controllerUtils.getUser(any())).thenReturn(user);
    }

    @Test
    void testUpdateUserInfo() throws Exception {
        UserUpdate userUpdate = new UserUpdate();
        userUpdate.setFirstName("John");
        userUpdate.setSecondName("Test Second Name");
        userUpdate.setEmail("email");
        userUpdate.setFirstName("John");

        User targetUser = new User();
        targetUser.setEmail("john.doe@example.com");

        doNothing().when(userAccountService).updateUser(any(User.class), any(UserUpdate.class));
        when(jwtUtils.generateJwtToken(anyString())).thenReturn("jwt-token");
        when(controllerUtils.createJwtCookie("jwt-token")).thenCallRealMethod();

        mockMvc.perform(put("/api/v1/account/update-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userUpdate))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(cookie().value("jwt", "jwt-token"));
    }

    @Test
    void testSetUserLogo() throws Exception {
        MockMultipartFile logo = new MockMultipartFile(
                "file", "logo.png", MediaType.IMAGE_PNG_VALUE, "logo content".getBytes());

        doNothing().when(userAccountService).setUserLogo(any(User.class), any(MultipartFile.class));

        mockMvc.perform(multipart("/api/v1/account/logo")
                        .file(logo)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserLogo() throws Exception {
        User user = new User();
        StorageService.BytesWithContentType file = new StorageService.BytesWithContentType(
                "logo content".getBytes(), MediaType.IMAGE_PNG_VALUE);

        when(userAccountService.getUserLogo(any(User.class))).thenReturn(file);
        when(controllerUtils.getResponseEntityFromFile(anyString(), any(StorageService.BytesWithContentType.class)))
                .thenCallRealMethod();

        mockMvc.perform(get("/api/v1/account/logo")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE));
    }

    @Test
    void testChangePassword() throws Exception {
        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setOldPassword("oldPassword");
        passwordDto.setNewPassword("newPassword");

        when(userAccountService.changeUserPassword(any(User.class), any(PasswordDto.class))).thenReturn(true);

        mockMvc.perform(put("/api/v1/account/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(passwordDto))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserInfo() throws Exception {
        NamedDto namedDto = new NamedDto();
        namedDto.setId(1L);
        namedDto.setName("comp1");

        NamedDto namedDto1 = new NamedDto();
        namedDto1.setId(2L);
        namedDto1.setName("comp2");

        UserWithCompanyDto userWithCompanyDto = new UserWithCompanyDto();
        userWithCompanyDto.setFirstName("John");
        userWithCompanyDto.setEmail("test@mail.com");
        userWithCompanyDto.setCompanies(Set.of(namedDto, namedDto1));


        when(userAccountService.getUserInfo(anyLong())).thenReturn(new User());
        when(userMapper.mapToUserWithCompany(any(User.class))).thenReturn(userWithCompanyDto);

        mockMvc.perform(get("/api/v1/account")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(userWithCompanyDto.getFirstName()))
                .andExpect(jsonPath("$.email").value(userWithCompanyDto.getEmail()))
                .andExpect(jsonPath("$.companies").isArray())
                .andExpect(jsonPath("$.companies").isNotEmpty());
    }
}
