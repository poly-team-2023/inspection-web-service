package com.service.inspection.controller;

import com.service.inspection.dto.UserSignInDto;
import com.service.inspection.dto.UserSignUpDto;
import com.service.inspection.mapper.UserMapper;
import com.service.inspection.service.AuthService;
import com.service.inspection.utils.ControllerUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;
    private final ControllerUtils controllerUtils;

    @PostMapping("/sign-in")
    public ResponseEntity<Void> authUser(@RequestBody @Valid UserSignInDto log,
                                         HttpServletResponse httpServletResponse) {
        String jwt = authService.auth(log.getEmail(), log.getPassword());
        httpServletResponse.addCookie(controllerUtils.createJwtCookie(jwt));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Void> registerUser(@RequestBody @Valid UserSignUpDto reg) {
        authService.createNewUser(userMapper.mapToUser(reg));
        return ResponseEntity.ok().build();
    }
}
