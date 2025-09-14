package com.management.houserent.repository;

import com.management.houserent.model.RoomApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomApplicationRepository extends JpaRepository<RoomApplication, Long> {
    List<RoomApplication> findByRoom_Owner_Id(Long ownerId);
    List<RoomApplication> findByTenant_Id(Long tenantId);

    List<RoomApplication> findByRoom_Id(Long id);
}
