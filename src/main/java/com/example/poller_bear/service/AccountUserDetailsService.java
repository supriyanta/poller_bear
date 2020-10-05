package com.example.poller_bear.service;

import com.example.poller_bear.model.AccountUser;
import com.example.poller_bear.repository.AccountUserRepository;
import com.example.poller_bear.security.AccountUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountUserDetailsService implements UserDetailsService {

    @Autowired
    public AccountUserRepository accountUserRepository;

    @Transactional
    @Override
    public AccountUserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        AccountUser user = accountUserRepository
                .findByEmailOrUsername(emailOrUsername, emailOrUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return AccountUserDetails.build(user);
    }

    @Transactional
    public AccountUserDetails loadByUserId(Long userId) {
        AccountUser user = accountUserRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return AccountUserDetails.build(user);
    }
}
