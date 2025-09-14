package com.management.houserent.service;

import com.management.houserent.dto.BillRequestDto;
import com.management.houserent.dto.BillResponseDto;
import com.management.houserent.exception.ResourceNotFoundException;
import com.management.houserent.model.Bill;
import com.management.houserent.model.Lease;
import com.management.houserent.repository.BillRepository;
import com.management.houserent.repository.LeaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillServiceImpl implements BillService{

    private final BillRepository billRepo;
    private final LeaseRepository leaseRepo;

    public BillServiceImpl(BillRepository billRepo, LeaseRepository leaseRepo) {
        this.billRepo = billRepo;
        this.leaseRepo = leaseRepo;
    }


    @Override
    @Transactional
    public BillResponseDto createBill(BillRequestDto dto) {

        Lease lease = leaseRepo.findById(dto.getLeaseId())
                .orElseThrow(()->new ResourceNotFoundException("Lease not found " + dto.getLeaseId()));

        Bill bill = new Bill();
        bill.setLease(lease);
        bill.setType(Bill.BillType.valueOf(dto.getType().toUpperCase()));
        bill.setAmount(dto.getAmount());
        bill.setDueDate(dto.getDueDate());
        bill = billRepo.save(bill);

        return mapToDto(bill);
    }


    @Override
    @Transactional
    public List<BillResponseDto> getBillsForLease(Long leaseId) {
        return billRepo.findByLease_id(leaseId).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public BillResponseDto payBill(long billId) {
        Bill bill = billRepo.findById(billId)
                .orElseThrow(()->new ResourceNotFoundException("Bills Not found " + billId));
        bill.setStatus(Bill.BillStatus.PAID);
        bill = billRepo.save(bill);
        return mapToDto(bill);
    }


    private BillResponseDto mapToDto(Bill bill) {

        BillResponseDto dto  = new BillResponseDto();

        dto.setId(bill.getId());
        dto.setLeaseId(bill.getLease().getId());
        dto.setType(bill.getType().name());
        dto.setAmount(bill.getAmount());
        dto.setStatus(bill.getStatus().name());
        dto.setDueDate(bill.getDueDate());
        dto.setCreatedAt(bill.getCreatedAt());
        dto.setUpdatedAt(bill.getUpdatedAt());

        return dto;
    }
}
