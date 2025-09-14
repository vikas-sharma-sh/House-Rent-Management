package com.management.houserent.service;

import com.management.houserent.dto.TenantRequestDto;
import com.management.houserent.dto.TenantResponseDto;

import java.util.List;

public interface TenantService {

    TenantResponseDto updateTenant(Long id, TenantRequestDto dto);
    TenantResponseDto getTenantByEmail(String email);
}
