package com.example.poller_bear.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "votes")
@Getter
@Setter
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(name = "poll_id", nullable = false, unique = true)
    private Poll poll;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = true
    )
    @JoinColumn(name = "choice_id", nullable = false, unique = true)
    private Choice choice;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(name = "user_id", nullable = false)
    private AccountUser user;

    public Vote() {
    }
}
