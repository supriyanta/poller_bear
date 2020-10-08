package com.example.poller_bear.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class PollDuration {

    @NotNull
//    @Size(min = 0, max = 15)
    private Long days = 1L;

    @NotNull
//    @Size(min = 0, max = 23)
    private Long hours = 1L;

    public PollDuration() {
    }

    public PollDuration(@NotNull @Size(min = 0, max = 15) Long days,
                        @NotNull @Size(min = 0, max = 23) Long hours) {
        this.days = days;
        this.hours = hours;
    }
}
