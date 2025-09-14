package com.management.houserent.service;

import com.management.houserent.dto.RoomRequestDto;
import com.management.houserent.dto.RoomResponseDto;

import java.util.List;

public interface RoomService {
    List<RoomResponseDto> getRoomsByOwnerEmail(String email);
    RoomResponseDto createRoom(String ownerEmail, RoomRequestDto req);
    RoomResponseDto updateRoomOwned(String ownerEmail, Long roomId, RoomRequestDto req);
    RoomResponseDto markAvailabilityOwned(String ownerEmail, Long roomId, boolean available);
    List<RoomResponseDto> getAvailableRoomsPublic();
    RoomResponseDto getRoomPublicById(Long id);
    List<RoomResponseDto> getAvailableRooms();
}
