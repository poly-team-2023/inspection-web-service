package com.service.inspection.utils;

import com.service.inspection.entities.User;
import com.service.inspection.service.StorageService;
import com.service.inspection.service.security.UserDetailsImpl;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

    public Long getUserId(Authentication authentication) {
        return ((UserDetailsImpl) authentication.getPrincipal()).getUser().getId();
    }

    public User getUser(Authentication authentication) {
        return ((UserDetailsImpl) authentication.getPrincipal()).getUser();
    }

    public ResponseEntity<Resource> getResponseEntityFromFile(String filename, StorageService.BytesWithContentType file) {
        if (file == null) {
            return ResponseEntity.ok().build();
        }
        Resource resource = new ByteArrayResource(file.getBytes());
        return ResponseEntity.ok()
                .header("Content-Disposition", String.format("attachment; filename=%s.png", filename))
                .contentType(MediaType.parseMediaType(Optional.of(file.getContentType()).orElse(MediaType.ALL.toString())))
                .body(resource);
    }
}
