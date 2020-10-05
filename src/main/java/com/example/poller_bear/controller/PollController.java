package com.example.poller_bear.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/poll")
public class PollController {

    // TODO : delete after test
    @Secured("ROLE_USER")
    @GetMapping("/check")
    public String check() {
        return "Checked";
    }
}
