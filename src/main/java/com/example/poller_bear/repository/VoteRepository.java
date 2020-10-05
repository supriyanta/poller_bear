package com.example.poller_bear.repository;

import com.example.poller_bear.model.Poll;
import com.example.poller_bear.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
}
