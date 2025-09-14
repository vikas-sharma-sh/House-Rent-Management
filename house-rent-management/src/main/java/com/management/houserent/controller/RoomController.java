package com.management.houserent.controller;

import com.management.houserent.dto.RoomResponseDto;
import com.management.houserent.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;
    public RoomController(RoomService roomService) { this.roomService = roomService; }

    @GetMapping("/available")
    public ResponseEntity<List<RoomResponseDto>> available() {
        return ResponseEntity.ok(roomService.getAvailableRoomsPublic()); // no sensitive owner info
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDto> byId(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomPublicById(id));
    }
}
