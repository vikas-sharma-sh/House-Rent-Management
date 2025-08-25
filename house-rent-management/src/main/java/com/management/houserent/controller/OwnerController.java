package com.management.houserent.controller;


import com.management.houserent.dto.OwnerRequestDto;
import com.management.houserent.dto.OwnerResponseDto;
import com.management.houserent.service.OwnerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    @PostMapping
    public ResponseEntity<OwnerResponseDto> createOwner(@Valid @RequestBody OwnerRequestDto dto){
        return ResponseEntity.ok(ownerService.createOwner(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OwnerResponseDto> getOwnerById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ownerService.getOwnerById(id));
    }

    @GetMapping
    public ResponseEntity<List<OwnerResponseDto>> getAllOwners(){
        return  ResponseEntity.ok(ownerService.getAllOwners());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OwnerResponseDto> updateOwner(@PathVariable Long id , @Valid @RequestBody OwnerRequestDto dto){
         return  ResponseEntity.ok(ownerService.updateOwner(id,dto));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOwner(@PathVariable Long id){
        ownerService.deleteOwner(id);
        return  ResponseEntity.ok("Owner deleted successfully");
    }


}
