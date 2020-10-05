package com.example.poller_bear.dto;

import com.example.poller_bear.model.AccountUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupResponse {

    private String name;

    private String username;

    private String email;

    public SignupResponse() {
    }

    public SignupResponse(String name, String username, String email) {
        this.name = name;
        this.username = username;
        this.email = email;
    }

    public SignupResponse(AccountUser user) {
        this.name = user.getName();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
