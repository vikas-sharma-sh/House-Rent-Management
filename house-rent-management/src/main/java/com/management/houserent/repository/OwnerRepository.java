package com.management.houserent.repository;

import com.management.houserent.model.Owner;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    boolean existsByEmail(String email);
    Optional<Owner> findByEmail(String email);
    Optional<Owner> findByUser_Email(String email);
}
