package com.management.houserent.controller;

import com.management.houserent.dto.RoomRequestDto;
import com.management.houserent.dto.RoomResponseDto;
import com.management.houserent.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<RoomResponseDto> createRoom(@RequestBody RoomRequestDto request) {
        return ResponseEntity.ok(roomService.createRoom(request));
    }

    @PutMapping("/{roomId}/assign/{tenantId}")
    public ResponseEntity<RoomResponseDto> assignTenant(@PathVariable Long roomId, @PathVariable Long tenantId) {
        return ResponseEntity.ok(roomService.assignTenant(roomId, tenantId));
    }

    @PutMapping("/{roomId}/unassign")
    public ResponseEntity<RoomResponseDto> unassignTenant(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.unassignTenant(roomId));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<RoomResponseDto>> getRoomsByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(roomService.getRoomsByOwner(ownerId));
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomResponseDto>> getAvailableRooms() {
        return ResponseEntity.ok(roomService.getAvailableRooms());
    }
}
