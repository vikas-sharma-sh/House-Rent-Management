package com.management.houserent.controller;


import com.management.houserent.dto.OwnerRequestDto;
import com.management.houserent.dto.OwnerResponseDto;
import com.management.houserent.service.OwnerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OwnerResponseDto> createOwner(@Valid @RequestBody OwnerRequestDto dto){
        return ResponseEntity.ok(ownerService.createOwner(dto));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('OWNER')")
    public OwnerResponseDto getMyOwnerProfile(Authentication authentication) {
        String email = authentication.getName(); // email from JWT
        return ownerService.getOwnerByEmail(email);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OwnerResponseDto> getOwnerById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ownerService.getOwnerById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OwnerResponseDto>> getAllOwners(){
        return  ResponseEntity.ok(ownerService.getAllOwners());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('OWNER') and #id == principal.id)")
    public ResponseEntity<OwnerResponseDto> updateOwner(@PathVariable Long id , @Valid @RequestBody OwnerRequestDto dto){
         return  ResponseEntity.ok(ownerService.updateOwner(id,dto));

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteOwner(@PathVariable Long id){
        ownerService.deleteOwner(id);
        return  ResponseEntity.ok("Owner deleted successfully");
    }


}
