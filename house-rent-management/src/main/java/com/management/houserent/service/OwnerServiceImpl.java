package com.management.houserent.service;

import com.management.houserent.dto.OwnerRequestDto;
import com.management.houserent.dto.OwnerResponseDto;
import com.management.houserent.exception.DuplicateResourceException;
import com.management.houserent.exception.ResourceNotFoundException;
import com.management.houserent.model.Owner;
import com.management.houserent.repository.OwnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepo;


    public OwnerServiceImpl(OwnerRepository ownerRepo) { this.ownerRepo = ownerRepo; }


    @Override
    @Transactional
    public OwnerResponseDto updateOwner(Long id, OwnerRequestDto request) {
        Owner o = ownerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + id));
        o.setName(request.getName());
        o.setPhone(request.getPhone());
        // do not allow email change easily; if allowed, ensure duplicates are checked
        Owner up = ownerRepo.save(o);
        return mapToDto(up);
    }


    @Override
    public OwnerResponseDto getOwnerByEmail(String email) {
        Owner o = ownerRepo.
                findByEmail(email.toLowerCase().trim()).
                orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + email));
        return mapToDto(o);
    }

    private OwnerResponseDto mapToDto(Owner o) {
        OwnerResponseDto dto = new OwnerResponseDto();
        dto.setId(o.getId());
        dto.setName(o.getName());
        dto.setEmail(o.getEmail());
        dto.setPhone(o.getPhone());
        dto.setCreatedAt(o.getCreatedAt());
        dto.setUpdatedAt(o.getUpdatedAt());
        return dto;
    }

    /*
     Room.AvailabilityStatus s = Room.AvailabilityStatus.valueOf(status.trim().toUpperCase());
            room.setAvailabilityStatus(s);
            rooms.save(room);
     */
}
