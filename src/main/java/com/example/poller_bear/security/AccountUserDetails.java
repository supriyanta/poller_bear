package com.example.poller_bear.security;

import com.example.poller_bear.model.AccountUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
@Getter
@Setter
public class AccountUserDetails implements UserDetails {

    private Long id;

    private String name;

    private String username;

    private String email;

    private String password;

    public Collection<? extends GrantedAuthority> authorities;

    public static AccountUserDetails build(AccountUser user) {
        AccountUserDetails userDetails = new AccountUserDetails();

        userDetails.setId(user.getId());
        userDetails.setName(user.getName());
        userDetails.setEmail(user.getEmail());
        userDetails.setUsername(user.getUsername());
        userDetails.setPassword(user.getPassword());

        Collection<GrantedAuthority> authorities = user.getRoles()
                .stream().map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        userDetails.setAuthorities(authorities);
        return userDetails;
    }

    public AccountUserDetails() {
    }

    public AccountUserDetails(Long id, String name, String username, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
