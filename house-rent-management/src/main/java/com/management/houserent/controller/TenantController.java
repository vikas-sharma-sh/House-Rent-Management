package com.management.houserent.controller;

import com.management.houserent.dto.TenantRequestDto;
import com.management.houserent.dto.TenantResponseDto;
import com.management.houserent.dto.RoomResponseDto;
import com.management.houserent.dto.ApplicationResponseDto;

import com.management.houserent.service.TenantService;
import com.management.houserent.service.RoomService;
import com.management.houserent.service.ApplicationService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantService tenantService;
    private final RoomService roomService;
    private final ApplicationService applicationService;
  //  private final PaymentService paymentService;

    public TenantController(TenantService tenantService,
                            RoomService roomService,
                            ApplicationService applicationService) {
        this.tenantService = tenantService;
        this.roomService = roomService;
        this.applicationService = applicationService;
       // this.paymentService = paymentService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<TenantResponseDto> me(Authentication auth) {
        return ResponseEntity.ok(tenantService.getTenantByEmail(auth.getName()));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<TenantResponseDto> updateMe(Authentication auth, @Valid @RequestBody TenantRequestDto dto) {
        var me = tenantService.getTenantByEmail(auth.getName());
        return ResponseEntity.ok(tenantService.updateTenant(me.getId(), dto));
    }

    // Browse rooms (tenant scope convenience; also available publicly in RoomController)
    @GetMapping("/browse")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<RoomResponseDto>> browse() {
        return ResponseEntity.ok(roomService.getAvailableRooms());
    }

    @GetMapping("/applications/my")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<ApplicationResponseDto>> myApplications(Authentication auth) {
        return ResponseEntity.ok(applicationService.getApplicationsForTenant(auth.getName()));
    }

//    @GetMapping("/payments/my")
//    public ResponseEntity<List<PaymentResponse>> myPayments(Authentication auth) {
//        return ResponseEntity.ok(paymentService.getPaymentsForTenant(auth.getName()));
//    }
}
