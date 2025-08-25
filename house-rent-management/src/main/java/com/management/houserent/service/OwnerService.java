package com.management.houserent.service;

import com.management.houserent.dto.OwnerRequestDto;
import com.management.houserent.dto.OwnerResponseDto;

import java.util.List;

public interface OwnerService {
    OwnerResponseDto createOwner(OwnerRequestDto dto);
    OwnerResponseDto getOwnerById(Long id) ;
    List<OwnerResponseDto> getAllOwners();
    OwnerResponseDto updateOwner(Long id , OwnerRequestDto dto);
    void deleteOwner(Long id);
}
