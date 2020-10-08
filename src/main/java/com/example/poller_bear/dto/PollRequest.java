package com.example.poller_bear.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PollRequest {

    @NotBlank
    @Size(max = 150)
    private String topic;

    @NotNull
    @Valid
    @Size(min = 2, max = 8)
    private List<ChoiceRequest> choices = new ArrayList<>();

    @NotNull
    @Valid
    private PollDuration duration;
}
