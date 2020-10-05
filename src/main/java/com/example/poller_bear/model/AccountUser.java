package com.example.poller_bear.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
public class AccountUser extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 30)
    @NotBlank
    private String name;

    @Size(max = 15)
    @NotBlank
    @Column(unique = true)
    private String username;

    @Email
    @NotBlank
    @Size(max = 45)
    @Column(unique = true)
    private String email;

    @NotBlank
    @Size(max = 60)
    private String password;

    @NotNull
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public AccountUser() {
    }

    public AccountUser(String name, String username, String email, String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
