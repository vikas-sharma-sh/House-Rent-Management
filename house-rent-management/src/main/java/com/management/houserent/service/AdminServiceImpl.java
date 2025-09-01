package com.management.houserent.service;

import com.management.houserent.dto.AdminStatsDto;
import com.management.houserent.model.Room;
import com.management.houserent.repository.OwnerRepository;
import com.management.houserent.repository.RoomRepository;
import com.management.houserent.repository.TenantRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService{
    private final OwnerRepository ownerRepo;
    private final TenantRepository tenantRepo;
    private final RoomRepository roomRepo;

    public AdminServiceImpl(OwnerRepository ownerRepo, TenantRepository tenantRepo, RoomRepository roomRepo) {
        this.ownerRepo = ownerRepo;
        this.tenantRepo = tenantRepo;
        this.roomRepo = roomRepo;
    }


    @Override
    public AdminStatsDto getStats() {
        AdminStatsDto dto = new AdminStatsDto();
        dto.setTotalOwner(ownerRepo.count());
        dto.setTotalTenants(tenantRepo.count());
        dto.setTotalRooms(roomRepo.count());

        dto.setOccupiedRooms(roomRepo.findByAvailabilityStatus(Room.AvailabilityStatus.OCCUPIED).size());

        dto.setAvailableRooms(roomRepo.findByAvailabilityStatus(Room.AvailabilityStatus.AVAILABLE).size());

        return dto;
    }
}
