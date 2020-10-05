package com.example.poller_bear.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginRequest {

    @NotBlank
    private String emailOrUsername;

    @NotBlank
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(@NotBlank String emailOrUsername, @NotBlank String password) {
        this.emailOrUsername = emailOrUsername;
        this.password = password;
    }
}
