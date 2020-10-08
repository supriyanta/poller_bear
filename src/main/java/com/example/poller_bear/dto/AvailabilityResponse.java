package com.example.poller_bear.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvailabilityResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean usernameAvailable = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean emailAvailable = null;

    public AvailabilityResponse() {
    }

    public AvailabilityResponse(Boolean usernameAvailable, Boolean emailAvailable) {
        this.usernameAvailable = usernameAvailable;
        this.emailAvailable = emailAvailable;
    }
}
