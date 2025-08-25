package com.management.houserent.service;


import com.management.houserent.dto.OwnerRequestDto;
import com.management.houserent.dto.OwnerResponseDto;
import com.management.houserent.exception.DuplicateResourceException;
import com.management.houserent.exception.ResourceNotFoundException;
import com.management.houserent.model.Owner;
import com.management.houserent.repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OwnerServiceImpl implements  OwnerService {

    @Autowired
    private OwnerRepository ownerRepository ;

    @Override
    public OwnerResponseDto createOwner(OwnerRequestDto dto) {
        if(ownerRepository.existsByEmail(dto.getEmail())){
            throw new DuplicateResourceException("Owner with email already exists!");
        }
        Owner owner = new Owner();
        owner.setName(dto.getName());
        owner.setEmail(dto.getEmail());
        owner.setPhone(dto.getPhone());

        Owner saved = ownerRepository.save(owner);

        return  mapToDto(saved);

    }

    @Override
    public OwnerResponseDto getOwnerById(Long id) {
         Owner owner = ownerRepository.findById(id)
                 .orElseThrow(()->new ResourceNotFoundException("Owner not found with id "+ id));
         return  mapToDto(owner);
    }

    @Override
    public List<OwnerResponseDto> getAllOwners() {
        return ownerRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public OwnerResponseDto updateOwner(Long id , OwnerRequestDto dto) {

        Owner owner = ownerRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Owner not found with id " + id));

        owner.setName(dto.getName());
        owner.setEmail(dto.getEmail());
        owner.setPhone(dto.getPhone());

        Owner updated = ownerRepository.save(owner);

        return mapToDto(updated);
    }


    @Override
    public void deleteOwner(Long id) {
        if(!ownerRepository.existsById(id)){
            throw  new ResourceNotFoundException("Owner not found with id " + id);
        };

        ownerRepository.deleteById(id);
    }

    //---mapper---
    private OwnerResponseDto mapToDto(Owner owner) {

        OwnerResponseDto dto = new OwnerResponseDto();
        dto.setId(owner.getId());
        dto.setName(owner.getName());
        dto.setEmail(owner.getEmail());
        dto.setPhone(owner.getPhone());
        dto.setCreatedAt(owner.getCreatedAt());
        dto.setUpdatedAt(owner.getUpdatedAt());

        return  dto;

    }






}
