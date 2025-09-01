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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final OwnerRepository ownerRepository;
    private final TenantRepository tenantRepository;

    public RoomServiceImpl(RoomRepository roomRepository,
                           OwnerRepository ownerRepository,
                           TenantRepository tenantRepository) {
        this.roomRepository = roomRepository;
        this.ownerRepository = ownerRepository;
        this.tenantRepository = tenantRepository;
    }

    @Override
    public RoomResponseDto createRoom(RoomRequestDto request) {
        // Validate owner exists
        Owner owner = ownerRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID " + request.getOwnerId()));

        // Check uniqueness for owner + address + roomNumber
        Optional<Room> existing = roomRepository.findByOwner_IdAndAddressAndRoomNumber(
                owner.getId(), request.getAddress(), request.getRoomNumber()
        );
        if (existing.isPresent()) {
            throw new DuplicateResourceException("Room '" + request.getRoomNumber() +
                    "' already exists at '" + request.getAddress() + "' for owner " + owner.getId());
        }

        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setAddress(request.getAddress());
        room.setRentAmount(request.getRentAmount());
        room.setAvailabilityStatus(Room.AvailabilityStatus.AVAILABLE);
        room.setOwner(owner);

        Room saved = roomRepository.save(room);
        return mapToDto(saved);
    }

    @Override
    public RoomResponseDto assignTenant(Long roomId, Long tenantId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID " + roomId));
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with ID " + tenantId));

        room.setTenant(tenant);
        room.setAvailabilityStatus(Room.AvailabilityStatus.OCCUPIED);

        Room updated = roomRepository.save(room);
        return mapToDto(updated);
    }

    @Override
    public RoomResponseDto unassignTenant(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID " + roomId));

        room.setTenant(null);
        room.setAvailabilityStatus(Room.AvailabilityStatus.AVAILABLE);

        Room updated = roomRepository.save(room);
        return mapToDto(updated);
    }

    @Override
    public List<RoomResponseDto> getRoomsByOwner(Long ownerId) {
        return roomRepository.findByOwner_Id(ownerId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomResponseDto> getRoomsByOwnerEmail(String email) {
        Owner owner = ownerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with email: " + email));

        return roomRepository.findByOwner_Id(owner.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomResponseDto> getAvailableRooms() {
        return roomRepository.findByAvailabilityStatus(Room.AvailabilityStatus.AVAILABLE)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomResponseDto> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private RoomResponseDto mapToDto(Room room) {
        RoomResponseDto dto = new RoomResponseDto();
        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setAddress(room.getAddress());
        dto.setRentAmount(room.getRentAmount());
        dto.setAvailabilityStatus(room.getAvailabilityStatus().name());
        dto.setOwnerId(room.getOwner() != null ? room.getOwner().getId() : null);
        dto.setTenantId(room.getTenant() != null ? room.getTenant().getId() : null);
        return dto;
    }
}
