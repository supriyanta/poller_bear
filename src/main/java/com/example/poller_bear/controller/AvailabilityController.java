package com.example.poller_bear.controller;

import com.example.poller_bear.dto.AvailabilityResponse;
import com.example.poller_bear.dto.ResponseDto;
import com.example.poller_bear.exception.BadRequestException;
import com.example.poller_bear.model.AccountUser;
import com.example.poller_bear.repository.UserRepository;
import com.example.poller_bear.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Email;

@RestController
@RequestMapping("/api/user/availability")
public class AvailabilityController {

    @Autowired
    AvailabilityService availabilityService;

    @GetMapping
    @PreAuthorize("isAnonymous()")
    public ResponseDto<?> checkUsernameAvailability(@RequestParam(value = "username", defaultValue = "") String username,
                                                    @RequestParam(value = "email", defaultValue = "") String email) {

        if(username.equals("") && email.equals("")) {
            throw new BadRequestException("username or email not found");
        }

        Boolean usernameAvailable = null;
        Boolean emailAvailable = null;

        // only username check
        if(username.equals("") == false) {
            usernameAvailable = availabilityService.checkUsernameAvailability(username);
        }

        // only email check
        if(email.equals("") == false) {
            emailAvailable = availabilityService.checkEmailAvailability(email);
        }

        AvailabilityResponse availabilityResponse = new AvailabilityResponse(usernameAvailable, emailAvailable);

        return new ResponseDto<>(HttpStatus.OK, "success", availabilityResponse);
    }
}
