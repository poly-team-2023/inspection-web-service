package com.service.inspection.controller;

import com.service.inspection.dto.account.PasswordDto;
import com.service.inspection.dto.account.UserUpdate;
import com.service.inspection.entities.User;
import com.service.inspection.service.StorageService;
import com.service.inspection.service.UserAccountService;
import com.service.inspection.service.UserDetailsImpl;
import com.service.inspection.utils.ControllerUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @PutMapping("/update-user")
    public ResponseEntity<Void> updateUserInfo(
            @RequestBody @Valid UserUpdate userUpdate,
            Authentication authentication, HttpServletResponse httpServletResponse
    ) {
        User targetUser = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        userAccountService.updateUser(targetUser, userUpdate);
        httpServletResponse.addCookie(controllerUtils.createJwtCookie(userUpdate.getEmail()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logo")
    public ResponseEntity<Void> setUserLogo(@RequestParam("file") MultipartFile logo, Authentication authentication) {
        User targetUser = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        userAccountService.setUserLogo(targetUser, logo); // TODO проверка типа файла (можно только изображение)
        return ResponseEntity.ok().build();
    }

    @GetMapping("/logo")
    public ResponseEntity<Resource> getUserLogo(Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        StorageService.BytesWithContentType file = userAccountService.getUserLogo(user);

        if (file == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Resource byteArrayResource = new ByteArrayResource(file.getBytes());
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=logo,png")
                .contentType(MediaType.valueOf("image/png"))
                .body(byteArrayResource);
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@RequestBody PasswordDto passwordDto,  Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        boolean wasUpdated = userAccountService.changeUserPassword(user, passwordDto);
        if (!wasUpdated) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            // TODO обработка неправильно введенного пароля
        }
        return ResponseEntity.ok().build();
    }
}
