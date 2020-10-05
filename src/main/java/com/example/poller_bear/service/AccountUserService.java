package com.example.poller_bear.service;

import com.example.poller_bear.model.AccountUser;
import com.example.poller_bear.repository.AccountUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountUserService {

    @Autowired
    public AccountUserRepository accountUserRepository;

    public boolean isUsernameExists(String username) {
        return accountUserRepository.existsByUsername(username);
    }


    public boolean isEmailExists(String email) {
        return accountUserRepository.existsByEmail(email);
    }
}
