package com.example.poller_bear.repository;

import com.example.poller_bear.model.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {

    Optional<Poll> findById(Long pollId);

    long countByCreatedBy(Long userId);

    Page<Poll> findByCreatedBy(Long id, Pageable pageable);

    Page<Poll> findByIdIn(List<Long> pollIds, Pageable pageable);
}
