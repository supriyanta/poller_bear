package com.example.poller_bear.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChoiceResponse {

    private Long id;
    private String text;
    private Long voteCount;

    public ChoiceResponse() {
    }

    public ChoiceResponse(Long id, String text, Long voteCount) {
        this.id = id;
        this.text = text;
        this.voteCount = voteCount;
    }
}
