package com.example.poller_bear.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UserProfileResponse {

    private Long id;
    private String name;
    private String username;
    private Instant joinedAt;
    private Long pollCount;
    private Long voteCount;

    public UserProfileResponse(Long id, String name, String username, Instant joinedAt, Long pollCount, Long voteCount) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.joinedAt = joinedAt;
        this.pollCount = pollCount;
        this.voteCount = voteCount;
    }
}
