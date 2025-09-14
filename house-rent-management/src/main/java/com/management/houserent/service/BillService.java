package com.management.houserent.service;

import com.management.houserent.dto.BillRequestDto;
import com.management.houserent.dto.BillResponseDto;

import java.util.List;

public interface BillService {
    BillResponseDto createBill(BillRequestDto dto);
    List<BillResponseDto> getBillsForLease(Long leaseId);
    BillResponseDto payBill(long billId);
}
