package com.management.houserent.repository;

import com.management.houserent.model.Tenant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    boolean existsByEmail(String email);
    Optional<Tenant> findByEmail(String email);

}
