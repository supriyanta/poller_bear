package com.example.poller_bear.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Getter
@Setter
public class SignupRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, max = 10)
    private String password;

    public SignupRequest() {
    }

    public SignupRequest(@NotBlank String name, @NotBlank String username, @Email @NotBlank String email, @NotBlank @Size(min = 6, max = 10) String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
