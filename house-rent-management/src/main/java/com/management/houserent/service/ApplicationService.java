package com.management.houserent.service;

import com.management.houserent.dto.ApplicationRequestDto;
import com.management.houserent.dto.ApplicationResponseDto;

import java.util.List;

public interface ApplicationService {
    ApplicationResponseDto applyToRoom(String tenantEmail, ApplicationRequestDto req);
    List<ApplicationResponseDto> getApplicationsForOwner(String ownerEmail);
    List<ApplicationResponseDto> getApplicationsForTenant(String tenantEmail);
    ApplicationResponseDto approveApplication(Long applicationId, String ownerEmail);
    ApplicationResponseDto rejectApplication(Long applicationId, String ownerEmail);
}
