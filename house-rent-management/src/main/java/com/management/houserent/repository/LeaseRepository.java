package com.management.houserent.repository;

import com.management.houserent.model.Lease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaseRepository extends JpaRepository<Lease, Long> {
    Optional<Lease> findByRoom_Id(Long roomId);
    boolean existsByRoom_IdAndStatus(Long roomId, String status);
    List<Lease> findByOwner_Id(Long ownerId);
    List<Lease> findByTenant_Id(Long tenantId);
}
