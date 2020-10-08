package com.example.poller_bear.service;

import com.example.poller_bear.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Service
public class AvailabilityService {

    @Autowired
    UserRepository userRepository;

    @PreAuthorize("isAnonymous()")
    public boolean checkUsernameAvailability(@NotBlank String username) {
        return userRepository.existsByUsernameNot(username);
    }

    @PreAuthorize("isAnonymous()")
    public boolean checkEmailAvailability(@NotBlank @Email String email) {
        return userRepository.existsByEmailNot(email);
    }
}
