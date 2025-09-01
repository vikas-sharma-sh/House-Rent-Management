package com.management.houserent.controller;

import com.management.houserent.dto.RoomRequestDto;
import com.management.houserent.dto.RoomResponseDto;
import com.management.houserent.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<RoomResponseDto> createRoom(@RequestBody RoomRequestDto request) {
        return ResponseEntity.ok(roomService.createRoom(request));
    }

    @PutMapping("/{roomId}/assign/{tenantId}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<RoomResponseDto> assignTenant(@PathVariable Long roomId, @PathVariable Long tenantId) {
        return ResponseEntity.ok(roomService.assignTenant(roomId, tenantId));
    }

    @PutMapping("/{roomId}/unassign")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<RoomResponseDto> unassignTenant(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.unassignTenant(roomId));
    }

    // Admin: get rooms by any owner ID
    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoomResponseDto>> getRoomsByOwnerId(@PathVariable Long ownerId) {
        return ResponseEntity.ok(roomService.getRoomsByOwner(ownerId));
    }

    // OWNER: see their own rooms by email from auth
    @GetMapping("/my-rooms")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<RoomResponseDto>> getMyRooms(Authentication auth) {
        String email = auth.getName(); // assumes owner email is the username
        return ResponseEntity.ok(roomService.getRoomsByOwnerEmail(email));
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomResponseDto>> getAvailableRooms() {
        return ResponseEntity.ok(roomService.getAvailableRooms());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoomResponseDto>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }
}
