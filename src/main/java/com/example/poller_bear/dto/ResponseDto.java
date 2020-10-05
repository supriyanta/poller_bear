package com.example.poller_bear.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ResponseDto<T> {

    private T data;

    private HttpStatus status;

    private String message;

    public ResponseDto() {
    }

    public ResponseDto(HttpStatus status, String message, T data) {
        this.data = data;
        this.status = status;
        this.message = message;
    }

    public ResponseDto(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
