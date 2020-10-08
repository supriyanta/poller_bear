package com.example.poller_bear.repository;

import com.example.poller_bear.model.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AccountUser, Long> {

    List<AccountUser> findByIdIn(List<Long> userIds);

    Optional<AccountUser> findByUsername(String username);

    boolean existsByUsernameNot(String username);

    boolean existsByEmailNot(String email);
}
