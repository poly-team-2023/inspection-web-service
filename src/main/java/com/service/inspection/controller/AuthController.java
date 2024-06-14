package com.service.inspection.controller;

import com.service.inspection.dto.auth.UserSignInDto;
import com.service.inspection.dto.auth.UserSignUpDto;
import com.service.inspection.configs.security.jwt.JwtUtils;
import com.service.inspection.mapper.UserMapper;
import com.service.inspection.service.AuthService;
import com.service.inspection.utils.ControllerUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    private final ControllerUtils controllerUtils;
    private final JwtUtils jwtUtils;

    @PostMapping("/sign-in")
    public ResponseEntity<Void> authUser(@RequestBody @Valid UserSignInDto log,
                                         HttpServletResponse httpServletResponse) {
        Authentication authentication = authService.auth(log.getEmail(), log.getPassword(), log.getSecretToken());
        Cookie cookie = controllerUtils.createJwtCookie(jwtUtils.generateJwtToken(authentication));
        httpServletResponse.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Void> registerUser(@RequestBody @Valid UserSignUpDto reg) {
        authService.createNewUser(userMapper.mapToUser(reg));
        return ResponseEntity.ok().build();
    }
}
