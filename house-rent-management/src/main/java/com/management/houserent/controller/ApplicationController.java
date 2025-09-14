package com.management.houserent.controller;

import com.management.houserent.dto.ApplicationRequestDto;
import com.management.houserent.dto.ApplicationResponseDto;
import com.management.houserent.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/apply")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<ApplicationResponseDto> apply(Authentication auth,
                                                        @RequestBody ApplicationRequestDto req) {
        return ResponseEntity.ok(applicationService.applyToRoom(auth.getName(), req));
    }

    @GetMapping("/owner")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<ApplicationResponseDto>> forOwner(Authentication auth) {
        return ResponseEntity.ok(applicationService.getApplicationsForOwner(auth.getName()));
    }

    @GetMapping("/tenant")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<ApplicationResponseDto>> forTenant(Authentication auth) {
        return ResponseEntity.ok(applicationService.getApplicationsForTenant(auth.getName()));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApplicationResponseDto> approve(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(applicationService.approveApplication(id, auth.getName()));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApplicationResponseDto> reject(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(applicationService.rejectApplication(id, auth.getName()));
    }
}

