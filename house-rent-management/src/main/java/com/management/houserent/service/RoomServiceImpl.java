package com.management.houserent.service;

import com.management.houserent.dto.RoomRequestDto;
import com.management.houserent.dto.RoomResponseDto;
import com.management.houserent.exception.DuplicateResourceException;
import com.management.houserent.exception.ResourceNotFoundException;
import com.management.houserent.model.Owner;
import com.management.houserent.model.Room;
import com.management.houserent.model.Tenant;
import com.management.houserent.repository.OwnerRepository;
import com.management.houserent.repository.RoomRepository;
import com.management.houserent.repository.TenantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepo;
    private final OwnerRepository ownerRepo;


    public RoomServiceImpl(RoomRepository roomRepo, OwnerRepository ownerRepo) {
        this.roomRepo = roomRepo;
        this.ownerRepo = ownerRepo;
    }

    @Override
    @Transactional
    public RoomResponseDto createRoom(String ownerEmail, RoomRequestDto request) {
        Owner owner = ownerRepo.
                findByUser_Email(ownerEmail).
                orElseThrow(() -> new ResourceNotFoundException("Owner profile not found for: " + ownerEmail));
        Optional<Room> exists = roomRepo.
                findByOwner_IdAndAddressAndRoomNumber(owner.getId(), request.getAddress(), request.getRoomNumber());
        if (exists.isPresent()) throw new DuplicateResourceException("Room already exists for this owner at that address and number");
        Room r = new Room();
        r.setRoomNumber(request.getRoomNumber());
        r.setAddress(request.getAddress());
        r.setRentAmount(request.getRentAmount());
        r.setAvailabilityStatus(Room.AvailabilityStatus.AVAILABLE);
        r.setDepositAmount(request.getDepositAmount());
        r.setOwner(owner);
        Room saved = roomRepo.save(r);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public RoomResponseDto updateRoomOwned(String ownerEmail, Long roomId, RoomRequestDto request) {
        Owner owner = ownerRepo.findByEmail(ownerEmail)
                .orElseThrow(() -> new EntityNotFoundException("Owner not found"));

        Room room = roomRepo.findByIdAndOwner(roomId, owner)
                .orElseThrow(() -> new EntityNotFoundException("Room not found or not owned by you"));

        room.setRoomNumber(request.getRoomNumber());
        room.setAddress(request.getAddress());
        room.setRentAmount(request.getRentAmount());
        room.setDepositAmount(request.getDepositAmount());
        room.setAvailabilityStatus(
                request.getAvailabilityStatus() != null ? request.getAvailabilityStatus() : room.getAvailabilityStatus()
        );

        // Set availability status if provided
        if (request.getAvailabilityStatus() != null) {
            room.setAvailabilityStatus(request.getAvailabilityStatus());

            // ✅ Clear tenant if new status is not AVAILABLE
            if (request.getAvailabilityStatus() != Room.AvailabilityStatus.AVAILABLE && room.getTenant() != null) {
                room.setTenant(null);
            }
        }
        room.setUpdatedAt(java.time.LocalDateTime.now());

        Room updated = roomRepo.save(room);
        return mapToDto(updated);



    }

    @Override
    @Transactional
    public RoomResponseDto markAvailabilityOwned(String ownerEmail, Long roomId, boolean available) {
        Owner owner = ownerRepo.findByEmail(ownerEmail)
                .orElseThrow(()->new EntityNotFoundException("owner not found"));

        Room r = roomRepo.findByIdAndOwner(roomId,owner)
                .orElseThrow(()-> new EntityNotFoundException("Room not found or not owned by you"));

        r.setAvailabilityStatus(available ? Room.AvailabilityStatus.AVAILABLE : Room.AvailabilityStatus.UNDER_MAINTENANCE);

        if (r.getAvailabilityStatus() != Room.AvailabilityStatus.AVAILABLE && r.getTenant() != null) {
            r.setTenant(null);
        }

        Room updated = roomRepo.save(r);
        return mapToDto(updated);

    }


    @Override
    public List<RoomResponseDto> getRoomsByOwnerEmail(String ownerEmail) {
        Owner owner = ownerRepo.findByUser_Email(ownerEmail).orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + ownerEmail));
        return roomRepo.findByOwner_Id(owner.getId()).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<RoomResponseDto> getAvailableRooms() {
        return roomRepo.findByAvailabilityStatus(Room.AvailabilityStatus.AVAILABLE).stream().map(this::mapToDto).collect(Collectors.toList());
    }


    @Override
    public List<RoomResponseDto> getAvailableRoomsPublic() {
        return roomRepo.findByAvailabilityStatus(Room.AvailabilityStatus.AVAILABLE)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }


    @Override
    public RoomResponseDto getRoomPublicById(Long id) {
        Room r = roomRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + id));
        return mapToDto(r);
    }

    private RoomResponseDto mapToDto(Room r) {
        RoomResponseDto dto = new RoomResponseDto();
        dto.setId(r.getId());
        dto.setRoomNumber(r.getRoomNumber());
        dto.setAddress(r.getAddress());
        dto.setRentAmount(r.getRentAmount());
        dto.setDepositAmount(r.getDepositAmount());
        dto.setAvailabilityStatus(r.getAvailabilityStatus());
        dto.setOwnerId(r.getOwner() != null ? r.getOwner().getId() : null);
        dto.setTenantId(r.getTenant() != null ? r.getTenant().getId() : null);
        dto.setCreatedAt(r.getCreatedAt());
        dto.setUpdatedAt(r.getUpdatedAt());
        return dto;
    }
}
