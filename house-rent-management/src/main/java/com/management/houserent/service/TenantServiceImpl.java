package com.management.houserent.service;

import com.management.houserent.dto.TenantRequestDto;
import com.management.houserent.dto.TenantResponseDto;
import com.management.houserent.exception.DuplicateResourceException;
import com.management.houserent.exception.ResourceNotFoundException;
import com.management.houserent.model.Tenant;
import com.management.houserent.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepo;
    public TenantServiceImpl(TenantRepository tenantRepo) { this.tenantRepo = tenantRepo; }





    @Override
    @Transactional
    public TenantResponseDto updateTenant(Long id, TenantRequestDto dto) {
        Tenant t = tenantRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + id));
        t.setName(dto.getName());
        t.setPhone(dto.getPhone());
        Tenant updated = tenantRepo.save(t);
        return mapToDto(updated);
    }



    @Override
    public TenantResponseDto getTenantByEmail(String email) {
        Tenant t = tenantRepo.
                findByEmail(email.toLowerCase().trim()).orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + email));
        return mapToDto(t);
    }

    private TenantResponseDto mapToDto(Tenant t) {
        TenantResponseDto dto = new TenantResponseDto();
        dto.setId(t.getId());
        dto.setName(t.getName());
        dto.setEmail(t.getEmail());
        dto.setPhone(t.getPhone());
        dto.setCreatedAt(t.getCreatedAt());
        dto.setUpdatedAt(t.getUpdatedAt());
        return dto;
    }
}
