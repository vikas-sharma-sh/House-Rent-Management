package com.management.houserent.controller;

import com.management.houserent.service.AdminService;
import com.management.houserent.dto.RoomResponseDto;
import com.management.houserent.dto.OwnerResponseDto;
import com.management.houserent.dto.TenantResponseDto;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService service;
    public AdminController(AdminService service) { this.service = service; }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> stats() {
        return ResponseEntity.ok(service.stats());
    }

    @GetMapping("/owners")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OwnerResponseDto>> allOwners() {
        return ResponseEntity.ok(service.getAllOwners());
    }

    @GetMapping("/tenants")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TenantResponseDto>> allTenants() {
        return ResponseEntity.ok(service.getAllTenants());
    }

    @GetMapping("/rooms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoomResponseDto>> allRooms() {
        return ResponseEntity.ok(service.getAllRooms());
    }

    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> blockUser(@PathVariable Long userId) {
        service.blockOrDeleteUser(userId);
        return ResponseEntity.ok("User removed/blocked");
    }

//    @GetMapping("/payments")
//    public ResponseEntity<List<PaymentResponse>> allPayments() {
//        return ResponseEntity.ok(service.getAllPayments());
//    }
}
