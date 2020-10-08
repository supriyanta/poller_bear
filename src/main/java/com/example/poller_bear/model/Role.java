package com.example.poller_bear.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length=30)
    private Rolename name;

    public Role() {
    }

    public Role(Rolename name) {
        this.name = name;
    }
}
