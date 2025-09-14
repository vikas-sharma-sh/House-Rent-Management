package com.management.houserent.service;

import com.management.houserent.dto.ApplicationRequestDto;
import com.management.houserent.dto.ApplicationResponseDto;
import com.management.houserent.exception.ResourceNotFoundException;
import com.management.houserent.model.*;
import com.management.houserent.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final RoomRepository roomRepo;
    private final TenantRepository tenantRepo;
    private final RoomApplicationRepository appRepo;
    private final OwnerRepository ownerRepo;
    private final LeaseRepository leaseRepo;
    private final PaymentRepository paymentRepo;

    public ApplicationServiceImpl(RoomRepository roomRepo,
                                  TenantRepository tenantRepo,
                                  RoomApplicationRepository appRepo,
                                  OwnerRepository ownerRepo, LeaseRepository leaseRepo, PaymentRepository paymentRepo) {
        this.roomRepo = roomRepo;
        this.tenantRepo = tenantRepo;
        this.appRepo = appRepo;
        this.ownerRepo = ownerRepo;
        this.leaseRepo = leaseRepo;
        this.paymentRepo = paymentRepo;
    }

    @Override
    @Transactional
    public ApplicationResponseDto applyToRoom(String tenantEmail, ApplicationRequestDto req) {
        Tenant tenant = tenantRepo.findByEmail(tenantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant profile not found: " + tenantEmail));

        Room room = roomRepo.findById(req.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + req.getRoomId()));

        RoomApplication app = new RoomApplication();
        app.setRoom(room);
        app.setTenant(tenant);
        app.setMessage(req.getMessage());
        app.setStatus(ApplicationStatus.PENDING);
        app.setCreatedAt(LocalDateTime.now());
        app.setUpdatedAt(LocalDateTime.now());

        RoomApplication saved = appRepo.save(app);
        return mapToDto(saved);
    }

    @Override
    public List<ApplicationResponseDto> getApplicationsForOwner(String ownerEmail) {
        Owner owner = ownerRepo.findByUser_Email(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + ownerEmail));

        return appRepo.findByRoom_Owner_Id(owner.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationResponseDto> getApplicationsForTenant(String tenantEmail) {
        Tenant tenant = tenantRepo.findByEmail(tenantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + tenantEmail));

        return appRepo.findByTenant_Id(tenant.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApplicationResponseDto approveApplication(Long applicationId, String ownerEmail) {
        RoomApplication app = appRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        // verify owner owns the room
        if (!app.getRoom().getOwner().getUser().getEmail().equalsIgnoreCase(ownerEmail)) {
            throw new ResourceNotFoundException("Owner does not own this room.");
        }

        // mark this app APPROVED
        app.setStatus(ApplicationStatus.APPROVED);
        app.setUpdatedAt(LocalDateTime.now());

        // assign tenant to room
        Room room = app.getRoom();
        room.setTenant(app.getTenant());
      //  room.setAvailabilityStatus(Room.AvailabilityStatus.OCCUPIED);
        room.setUpdatedAt(LocalDateTime.now());
        roomRepo.save(room);

        Lease lease = new Lease();
        lease.setRoom(room);
        lease.setOwner(room.getOwner());
        lease.setTenant(app.getTenant());
        lease.setStartDate(LocalDate.now());
        lease.setEndDate(LocalDate.now().plusMonths(11)); // default 11 months
        lease.setMonthlyRent(room.getRentAmount());
        lease.setDepositAmount(room.getDepositAmount());
        lease.setStatus(LeaseStatus.PENDING);
        lease.setCreatedAt(LocalDateTime.now());
        lease.setUpdatedAt(LocalDateTime.now());
        leaseRepo.save(lease);

        Payment deposit = new Payment();
        deposit.setLease(lease);
        deposit.setTenant(app.getTenant());
        deposit.setAmount(room.getDepositAmount());
        deposit.setPaymentType(PaymentType.DEPOSIT);
        deposit.setPaymentStatus(PaymentStatus.PENDING);
        deposit.setCreatedAt(LocalDateTime.now());
        deposit.setUpdatedAt(LocalDateTime.now());
        paymentRepo.save(deposit);

        // reject all other pending applications for this room
        List<RoomApplication> others = appRepo.findByRoom_Id(room.getId());
        for (RoomApplication other : others) {
            if (!Objects.equals(other.getId(), app.getId())) {
                other.setStatus(ApplicationStatus.REJECTED);
                other.setUpdatedAt(LocalDateTime.now());
                appRepo.save(other);
            }
        }

        RoomApplication updated = appRepo.save(app);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public ApplicationResponseDto rejectApplication(Long applicationId, String ownerEmail) {
        RoomApplication app = appRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        if (!app.getRoom().getOwner().getUser().getEmail().equalsIgnoreCase(ownerEmail)) {
            throw new ResourceNotFoundException("Owner does not own this room.");
        }

        app.setStatus(ApplicationStatus.REJECTED);
        app.setUpdatedAt(LocalDateTime.now());

        // If room was assigned to this tenant → clear assignment
        Room room = app.getRoom();
        if (room.getTenant() != null && Objects.equals(room.getTenant().getId(), app.getTenant().getId())) {
            room.setTenant(null);
            room.setAvailabilityStatus(Room.AvailabilityStatus.AVAILABLE);
            room.setUpdatedAt(LocalDateTime.now());
            roomRepo.save(room);
        }

        RoomApplication updated = appRepo.save(app);
        return mapToDto(updated);
    }

    private ApplicationResponseDto mapToDto(RoomApplication app) {
        ApplicationResponseDto dto = new ApplicationResponseDto();
        dto.setId(app.getId());
        dto.setRoomId(app.getRoom().getId());
        dto.setTenantId(app.getTenant().getId());
        dto.setMessage(app.getMessage());
        dto.setStatus(app.getStatus());
        dto.setCreatedAt(app.getCreatedAt());
        dto.setUpdatedAt(app.getUpdatedAt());

        // enrich for API readability
        dto.setRoomNumber(app.getRoom().getRoomNumber());
        dto.setRoomAddress(app.getRoom().getAddress());
        dto.setTenantName(app.getTenant().getName());
        dto.setTenantEmail(app.getTenant().getEmail());

        return dto;
    }
}
