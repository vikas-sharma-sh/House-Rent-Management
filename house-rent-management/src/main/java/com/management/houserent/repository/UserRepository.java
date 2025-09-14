package com.management.houserent.repository;

import com.management.houserent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmail(String email); // ✅ Add this line

    Optional<User> findByEmail(String email); // Optional: useful for login or checks
}
