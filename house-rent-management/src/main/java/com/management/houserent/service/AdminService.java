package com.management.houserent.service;

import com.management.houserent.dto.AdminStatsDto;
import com.management.houserent.dto.OwnerResponseDto;
import com.management.houserent.dto.RoomResponseDto;
import com.management.houserent.dto.TenantResponseDto;

import java.util.List;
import java.util.Map;

public interface AdminService {
    Map<String, Long> stats();
    List<OwnerResponseDto> getAllOwners();
    List<TenantResponseDto> getAllTenants();
    List<RoomResponseDto> getAllRooms();
    void blockOrDeleteUser(Long userId);
   // List<PaymentResponse> getAllPayments();
}
