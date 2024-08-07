package com.service.inspection.controller;

import com.service.inspection.dto.account.PasswordDto;
import com.service.inspection.dto.account.UserUpdate;
import com.service.inspection.dto.account.UserWithCompanyDto;
import com.service.inspection.entities.User;
import com.service.inspection.configs.security.jwt.JwtUtils;
import com.service.inspection.mapper.UserMapper;
import com.service.inspection.service.StorageService;
import com.service.inspection.service.UserAccountService;
import com.service.inspection.service.security.UserDetailsImpl;
import com.service.inspection.utils.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/account")
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
@AllArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final ControllerUtils controllerUtils;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;

    @PutMapping("/update-user")
    @Operation(summary = "Обновить данные пользователя", description = "Null поля также обновляются")
    public ResponseEntity<Void> updateUserInfo(
            @RequestBody @Valid UserUpdate userUpdate,
            Authentication authentication, HttpServletResponse httpServletResponse
    ) {
        User targetUser = controllerUtils.getUser(authentication);
        userAccountService.updateUser(targetUser, userUpdate);
        httpServletResponse.addCookie(controllerUtils.createJwtCookie(
                jwtUtils.generateJwtToken(targetUser.getEmail())
        ));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logo")
    @Operation(summary = "Установить аватарку пользователя")
    public ResponseEntity<Void> setUserLogo(@RequestParam("file") MultipartFile logo, Authentication authentication) {
        User targetUser = controllerUtils.getUser(authentication);
        userAccountService.setUserLogo(targetUser, logo); // TODO проверка типа файла (можно только изображение)
        return ResponseEntity.ok().build();
    }

    @GetMapping("/logo")
    @Operation(summary = "Получить аватарку пользователя")
    public ResponseEntity<Resource> getUserLogo(Authentication authentication) {
        User targetUser = controllerUtils.getUser(authentication);
        StorageService.BytesWithContentType file = userAccountService.getUserLogo(targetUser);
        return controllerUtils.getResponseEntityFromFile("logo", file);
    }

    @PutMapping("/password")
    @Operation(summary = "Изменение пароля пользователя")
    public ResponseEntity<Void> changePassword(@RequestBody PasswordDto passwordDto, Authentication authentication) {
        User targetUser = controllerUtils.getUser(authentication);
        boolean wasUpdated = userAccountService.changeUserPassword(targetUser, passwordDto);
        if (!wasUpdated) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            // TODO обработка неправильно введенного пароля
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Информация о пользователе c информацией об имеющихся компаниях")
    public ResponseEntity<UserWithCompanyDto> getUserInfo(Authentication authentication) {
        Long userId = controllerUtils.getUserId(authentication);
        User user = userAccountService.getUserInfo(userId);
        return ResponseEntity.ok(userMapper.mapToUserWithCompany(user));
    }
}
