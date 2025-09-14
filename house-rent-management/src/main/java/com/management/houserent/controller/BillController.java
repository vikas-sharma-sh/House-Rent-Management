package com.management.houserent.controller;


import com.management.houserent.dto.BillRequestDto;
import com.management.houserent.dto.BillResponseDto;
import com.management.houserent.service.BillService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillService billService ;


    public BillController(BillService billService) {
        this.billService = billService;
    }


    @PreAuthorize("hasRole('OWNER') Or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BillResponseDto> createBill (@RequestBody BillRequestDto dto){
        return ResponseEntity.ok(billService.createBill(dto));
    }

    @PreAuthorize(" hasRole('TENANT') or hasRole('OWNER') Or hasRole('ADMIN')")
    @GetMapping("/lease/{leaseId}")
    public ResponseEntity<List<BillResponseDto>> getBillsForLease(@PathVariable Long leaseId){
        return ResponseEntity.ok(billService.getBillsForLease(leaseId));
    }

    @PreAuthorize("hasRole('TENANT')")
    @PostMapping("/{billId}/pay")
    public ResponseEntity<BillResponseDto> payBill(@PathVariable Long billId) {
        return ResponseEntity.ok(billService.payBill(billId));
    }

}
