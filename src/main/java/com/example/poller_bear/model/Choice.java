package com.example.poller_bear.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "choices")
@Getter
@Setter
public class Choice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 40)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    public Choice() {
    }
}
