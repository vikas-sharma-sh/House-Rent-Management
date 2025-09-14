package com.management.houserent.controller;

import com.management.houserent.dto.OwnerRequestDto;
import com.management.houserent.dto.OwnerResponseDto;
import com.management.houserent.dto.RoomRequestDto;
import com.management.houserent.dto.RoomResponseDto;
import com.management.houserent.dto.ApplicationResponseDto;

import com.management.houserent.service.OwnerService;
import com.management.houserent.service.RoomService;
import com.management.houserent.service.ApplicationService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    private final OwnerService ownerService;
    private final RoomService roomService;

    public OwnerController(OwnerService ownerService,
                           RoomService roomService) {
        this.ownerService = ownerService;
        this.roomService = roomService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<OwnerResponseDto> me(Authentication auth) {
        return ResponseEntity.ok(ownerService.getOwnerByEmail(auth.getName()));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<OwnerResponseDto> updateMe(Authentication auth,
                                                     @Valid @RequestBody OwnerRequestDto dto) {
        var me = ownerService.getOwnerByEmail(auth.getName());
        return ResponseEntity.ok(ownerService.updateOwner(me.getId(), dto));
    }

    // Rooms
    @GetMapping("/my-rooms")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<RoomResponseDto>> myRooms(Authentication auth) {
        return ResponseEntity.ok(roomService.getRoomsByOwnerEmail(auth.getName()));
    }

    @PostMapping("/my-rooms")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<RoomResponseDto> createRoom(Authentication auth,
                                                      @Valid @RequestBody RoomRequestDto req) {
        return ResponseEntity.ok(roomService.createRoom(auth.getName(), req));
    }

    @PutMapping("/my-rooms/{roomId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<RoomResponseDto> updateRoom(Authentication auth,
                                                      @PathVariable Long roomId,
                                                      @Valid @RequestBody RoomRequestDto req) {
        return ResponseEntity.ok(roomService.updateRoomOwned(auth.getName(), roomId, req));
    }

    @PutMapping("/my-rooms/{roomId}/mark-available")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<RoomResponseDto> markAvailable(Authentication auth, @PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.markAvailabilityOwned(auth.getName(), roomId, true));
    }

    @PutMapping("/my-rooms/{roomId}/mark-unavailable")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<RoomResponseDto> markUnavailable(Authentication auth, @PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.markAvailabilityOwned(auth.getName(), roomId, false));
    }
}
