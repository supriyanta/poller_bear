package com.example.poller_bear.repository;

import com.example.poller_bear.model.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountUserRepository extends JpaRepository<AccountUser, Long> {
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<AccountUser> findById(Long id);

    Optional<AccountUser> findByUsername(String username);

    Optional<AccountUser> findByEmail(String email);

    Optional<AccountUser> findByEmailOrUsername(String email, String username);
}


