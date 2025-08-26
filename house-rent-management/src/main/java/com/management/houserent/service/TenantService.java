package com.management.houserent.service;

import com.management.houserent.dto.TenantRequestDto;
import com.management.houserent.dto.TenantResponseDto;

import java.util.List;

public interface TenantService {
    TenantResponseDto createTenant(TenantRequestDto dto);
    TenantResponseDto getTenantById(Long id);
    List<TenantResponseDto> getAllTenants();
    TenantResponseDto updateTenant(Long id, TenantRequestDto dto);
    void deleteTenant(Long id);
}
