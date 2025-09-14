package com.management.houserent.service;

import com.management.houserent.dto.LeaseRequestDto;
import com.management.houserent.dto.LeaseResponseDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LeaseService {
    LeaseResponseDto createLease(String ownerEmail, LeaseRequestDto dto);
    LeaseResponseDto terminateLease(String ownerEmail, Long leaseId, String reason);
    LeaseResponseDto getLease(Long leaseId, String requesterEmail);
    List<LeaseResponseDto> getLeasesForOwner(String ownerEmail);
    List<LeaseResponseDto> getLeasesForTenant(String tenantEmail);

    void generateAgreementPdf(String requesterEmail, Long leaseId) throws Exception;
    void uploadAgreementFile(String requesterEmail, Long leaseId, MultipartFile file) throws Exception;
    Resource downloadAgreementByLease(Long leaseId, String requesterEmail) throws Exception;
}
