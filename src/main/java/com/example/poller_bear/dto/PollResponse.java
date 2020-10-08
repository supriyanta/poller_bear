package com.example.poller_bear.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class PollResponse {

    private Long id;
    private String topic;
    private List<ChoiceResponse> choices;
    private Instant createdAt;
    private Instant expiredAt;
    private Boolean isExpired;

    private UserSummary createdBy;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long SelectedChoice;

    private Long totalVotes;

    public PollResponse() {
    }

    public PollResponse(Long id, String topic, List<ChoiceResponse> choices, Instant createdAt, Instant expiredAt, Boolean isExpired, UserSummary createdBy, Long selectedChoice, Long totalVotes) {
        this.id = id;
        this.topic = topic;
        this.choices = choices;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.isExpired = isExpired;
        this.createdBy = createdBy;
        SelectedChoice = selectedChoice;
        this.totalVotes = totalVotes;
    }
}
