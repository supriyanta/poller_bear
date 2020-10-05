package com.example.poller_bear.repository;

import com.example.poller_bear.model.Role;
import com.example.poller_bear.model.Rolename;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(Rolename name);
}
