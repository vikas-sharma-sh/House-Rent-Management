package com.management.houserent.repository;

import com.management.houserent.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner,Long> {
    boolean existsByEmail(String email);
    Optional<Owner> findByEmail(String email);

}
