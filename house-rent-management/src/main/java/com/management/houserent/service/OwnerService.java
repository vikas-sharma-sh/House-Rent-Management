package com.management.houserent.service;

import com.management.houserent.dto.OwnerRequestDto;
import com.management.houserent.dto.OwnerResponseDto;

import java.util.List;

public interface OwnerService {

    OwnerResponseDto updateOwner(Long id, OwnerRequestDto request);
    OwnerResponseDto getOwnerByEmail(String email);
}
