package com.example.poller_bear.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
@Setter
public class ErrorResponseDto {

    private String message;
    private String error;
    private int status;
    private Instant timestamp;

    public ErrorResponseDto(String message, HttpStatus httpStatus) {
        this.message = message;
        this.error = httpStatus.getReasonPhrase();
        this.status = httpStatus.value();
        this.timestamp = Instant.now();
    }
}
