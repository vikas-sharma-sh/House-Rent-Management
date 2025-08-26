package com.management.houserent.service;

import com.management.houserent.dto.TenantRequestDto;
import com.management.houserent.dto.TenantResponseDto;
import com.management.houserent.exception.DuplicateResourceException;
import com.management.houserent.exception.ResourceNotFoundException;
import com.management.houserent.model.Tenant;
import com.management.houserent.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Override
    public TenantResponseDto createTenant(TenantRequestDto dto) {
        if (tenantRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Tenant with email already exists!");
        }
        Tenant tenant = new Tenant();
        tenant.setName(dto.getName());
        tenant.setEmail(dto.getEmail());
        tenant.setPhone(dto.getPhone());

        Tenant saved = tenantRepository.save(tenant);
        return mapToDto(saved);
    }

    @Override
    public TenantResponseDto getTenantById(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id " + id));
        return mapToDto(tenant);
    }

    @Override
    public List<TenantResponseDto> getAllTenants() {
        return tenantRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TenantResponseDto updateTenant(Long id, TenantRequestDto dto) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id " + id));

        tenant.setName(dto.getName());
        tenant.setEmail(dto.getEmail());
        tenant.setPhone(dto.getPhone());

        Tenant updated = tenantRepository.save(tenant);
        return mapToDto(updated);
    }

    @Override
    public void deleteTenant(Long id) {
        if (!tenantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tenant not found with id " + id);
        }
        tenantRepository.deleteById(id);
    }

    private TenantResponseDto mapToDto(Tenant tenant) {
        TenantResponseDto dto = new TenantResponseDto();
        dto.setId(tenant.getId());
        dto.setName(tenant.getName());
        dto.setEmail(tenant.getEmail());
        dto.setPhone(tenant.getPhone());
        dto.setCreatedAt(tenant.getCreatedAt());
        dto.setUpdatedAt(tenant.getUpdatedAt());
        return dto;
    }
}
