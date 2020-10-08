package com.example.poller_bear.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ChoiceRequest {

    @NotBlank
    @Size(max = 50)
    private String text;

    public ChoiceRequest() {
    }

    public ChoiceRequest(@NotBlank @Size(max = 50) String text) {
        this.text = text;
    }
}
