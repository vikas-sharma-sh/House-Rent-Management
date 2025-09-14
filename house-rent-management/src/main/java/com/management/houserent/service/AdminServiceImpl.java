// AdminServiceImpl.java
package com.management.houserent.service;

import com.management.houserent.dto.OwnerResponseDto;
import com.management.houserent.dto.RoomResponseDto;
import com.management.houserent.dto.TenantResponseDto;
import com.management.houserent.model.Room;
import com.management.houserent.model.User;
import com.management.houserent.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {
    private final UserRepository users;
    private final OwnerRepository owners;
    private final TenantRepository tenants;
    private final RoomRepository rooms;

    public AdminServiceImpl(UserRepository users, OwnerRepository owners, TenantRepository tenants, RoomRepository rooms) {
        this.users = users; this.owners = owners; this.tenants = tenants; this.rooms = rooms;
    }
    @Override
    public Map<String, Long> stats() {
        return Map.of(
                "users", users.count(),
                "owners", owners.count(),
                "tenants", tenants.count(),
                "rooms", rooms.count(),
                "occupiedRooms", (long) rooms.findByAvailabilityStatus(Room.AvailabilityStatus.OCCUPIED).size(),
                "availableRooms", (long) rooms.findByAvailabilityStatus(Room.AvailabilityStatus.AVAILABLE).size()
        );
    }

    @Override
    public List<OwnerResponseDto> getAllOwners() {
        return owners.findAll().stream().map(o -> {
            OwnerResponseDto dto = new OwnerResponseDto();
            dto.setId(o.getId());
            dto.setName(o.getName());
            dto.setEmail(o.getEmail());
            dto.setPhone(o.getPhone());
            dto.setCreatedAt(o.getCreatedAt());
            dto.setUpdatedAt(o.getUpdatedAt());
            return dto;
        }).toList();
    }

    @Override
    public List<TenantResponseDto> getAllTenants() {
        return tenants.findAll().stream().map(t ->{
            TenantResponseDto dto = new TenantResponseDto();
            dto.setId(t.getId());
            dto.setName(t.getName());
            dto.setEmail(t.getEmail());
            dto.setPhone(t.getPhone());
            dto.setCreatedAt(t.getCreatedAt());
            dto.setUpdatedAt(t.getUpdatedAt());
            return dto;

        }).toList();
    }

    @Override
    public List<RoomResponseDto> getAllRooms() {
        return rooms.findAll().stream().map(r -> {
            RoomResponseDto dto = new RoomResponseDto();
            dto.setId(r.getId());
            dto.setRoomNumber(r.getRoomNumber());
            dto.setAddress(r.getAddress());
            dto.setRentAmount(r.getRentAmount());
            dto.setAvailabilityStatus(Room.AvailabilityStatus.valueOf(r.getAvailabilityStatus().name()));
            dto.setOwnerId(r.getOwner() != null ? r.getOwner().getId() : null);
            dto.setTenantId(r.getTenant() != null ? r.getTenant().getId() : null);
            return dto;
        }).toList();
    }

    @Override
    public void blockOrDeleteUser(Long userId) {
        // soft delete or block: here we will delete for MVP
        users.findById(userId).ifPresent(users::delete);
    }

}
