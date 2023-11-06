package com.service.inspection.utils;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ControllerUtils {

    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    public Cookie createJwtCookie(String jwt) {
        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setPath("/");
        cookie.setMaxAge(jwtExpirationMs);
        return cookie;
    }
}
