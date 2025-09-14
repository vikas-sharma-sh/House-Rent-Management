package com.management.houserent.controller;

import com.management.houserent.dto.LeaseRequestDto;
import com.management.houserent.dto.LeaseResponseDto;
import com.management.houserent.service.LeaseService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/leases")
public class LeaseController {

    private final LeaseService leaseService;


    public LeaseController(LeaseService leaseService) {
        this.leaseService = leaseService;
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<LeaseResponseDto> createLease(Authentication auth, @Valid @RequestBody LeaseRequestDto req) {
        return ResponseEntity.ok(leaseService.createLease(auth.getName(), req));
    }

    @PutMapping("/{leaseId}/terminate")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<LeaseResponseDto> terminateLease(Authentication auth,
                                                           @PathVariable Long leaseId,
                                                           @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(leaseService.terminateLease(auth.getName(), leaseId, reason));
    }

    @GetMapping("/{leaseId}")
    @PreAuthorize("hasAnyRole('OWNER','TENANT','ADMIN')")
    public ResponseEntity<LeaseResponseDto> getLease(Authentication auth, @PathVariable Long leaseId) {
        // Additional access checks can be done in service if necessary
        return ResponseEntity.ok(leaseService.getLease(leaseId, auth.getName()));
    }

    @GetMapping("/owner/me")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<LeaseResponseDto>> myLeasesOwner(Authentication auth) {
        return ResponseEntity.ok(leaseService.getLeasesForOwner(auth.getName()));
    }

    @GetMapping("/tenant/me")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<LeaseResponseDto>> myLeasesTenant(Authentication auth) {
        return ResponseEntity.ok(leaseService.getLeasesForTenant(auth.getName()));
    }


    @PostMapping("/{leaseId}/generate-agreement")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<String> generateAgreement(Authentication auth , @PathVariable Long leaseId)throws Exception{
        leaseService.generateAgreementPdf(auth.getName(), leaseId);
        return ResponseEntity.ok("Agreement generated : ") ;
    }

    @PostMapping("/{leaseId}/upload-agreement")
    @PreAuthorize("hasAnyRole('OWNER','TENANT')")
    public ResponseEntity<String> uploadAgreement(Authentication auth , @PathVariable Long leaseId, @RequestParam("file")MultipartFile file) throws Exception{
        leaseService.uploadAgreementFile(auth.getName(),leaseId,file);
        return ResponseEntity.ok("Agreement-Uploaded");
    }

    @GetMapping("/{leaseId}/agreement")
    @PreAuthorize("hasAnyRole('OWNER','TENANT','ADMIN')")
    public ResponseEntity<Resource> downloadAgreement(Authentication auth,
                                                      @PathVariable Long leaseId) throws Exception {
        Resource resource = leaseService.downloadAgreementByLease(leaseId, auth.getName());

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"")
                .header("Content-Type", "application/pdf")
                .body(resource);
    }

}
