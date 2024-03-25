package com.service.inspection.advice;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class MessageException extends RuntimeException {
    private HttpStatus status = HttpStatus.BAD_REQUEST;

    public MessageException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
