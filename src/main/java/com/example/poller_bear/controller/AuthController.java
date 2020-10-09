package com.example.poller_bear.controller;

import com.example.poller_bear.dto.*;
import com.example.poller_bear.exception.InternalException;
import com.example.poller_bear.exception.BadRequestException;
import com.example.poller_bear.model.AccountUser;
import com.example.poller_bear.model.Role;
import com.example.poller_bear.model.Rolename;
import com.example.poller_bear.repository.AccountUserRepository;
import com.example.poller_bear.repository.RoleRepository;
import com.example.poller_bear.security.JwtUtil;
import com.example.poller_bear.service.AccountUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AccountUserService accountUserService;

    @Autowired
    AccountUserRepository accountUserRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseDto<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        if(accountUserService.isUsernameExists(signupRequest.getUsername()) ) {
            String message = String.format("user with username: %s already exist", signupRequest.getUsername());
            throw new BadRequestException(message);
        }

        if(accountUserService.isEmailExists(signupRequest.getEmail())) {
            String message = String.format("user with email: %s already exist", signupRequest.getEmail());
            throw new BadRequestException(message);
        }

        AccountUser newUser = new AccountUser(signupRequest.getName(),
                                signupRequest.getUsername(),
                                signupRequest.getEmail(),
                                passwordEncoder.encode(signupRequest.getPassword())
                            );

        Role role = roleRepository.findByName(Rolename.ROLE_USER)
                        .orElseThrow(() -> new InternalException("role creation failed"));

        newUser.setRoles(Collections.singleton(role));

        AccountUser user = accountUserRepository.save(newUser);

        return new ResponseDto(HttpStatus.CREATED, "User created", new SignupResponse(user));
    }

    @PostMapping("/login")
    public ResponseDto<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                                                loginRequest.getEmailOrUsername(),
                                                                loginRequest.getPassword()
                                                            );
        Authentication auth = authenticationManager.authenticate(authentication);

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwtToken = jwtUtil.createToken(auth);

        return new ResponseDto<LoginResponse>(HttpStatus.ACCEPTED, "login success", new LoginResponse(jwtToken));
    }
}
