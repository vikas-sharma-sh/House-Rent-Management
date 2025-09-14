package com.management.houserent.repository;

import com.management.houserent.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill,Long> {
    List<Bill>findByLease_id(Long leaseId);

    Arrays findByLease_Id(Long id);
}
