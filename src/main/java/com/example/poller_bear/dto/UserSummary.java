package com.example.poller_bear.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSummary {

    private Long id;
    private String name;
    private String username;

    public UserSummary() {
    }

    public UserSummary(Long id, String name, String username) {
        this.id = id;
        this.name = name;
        this.username = username;
    }
}
