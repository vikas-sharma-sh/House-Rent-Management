package com.management.houserent.service;

import com.management.houserent.dto.RoomRequestDto;
import com.management.houserent.dto.RoomResponseDto;

import java.util.List;

public interface RoomService {
    RoomResponseDto createRoom(RoomRequestDto request);
    RoomResponseDto assignTenant(Long roomId,Long tenantId);
    RoomResponseDto unassignTenant(Long roomId);
    List<RoomResponseDto> getRoomsByOwner(Long ownerId);
    List<RoomResponseDto> getAvailableRooms();

    List<RoomResponseDto> getAllRooms();

    List<RoomResponseDto> getRoomsByOwnerEmail(String email);
}
