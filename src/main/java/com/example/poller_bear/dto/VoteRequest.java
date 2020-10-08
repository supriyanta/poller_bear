package com.example.poller_bear.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class VoteRequest {

    @NotNull
    private Long choiceId;

    public VoteRequest() {
    }

    public VoteRequest(Long choiceId) {
        this.choiceId = choiceId;
    }
}
