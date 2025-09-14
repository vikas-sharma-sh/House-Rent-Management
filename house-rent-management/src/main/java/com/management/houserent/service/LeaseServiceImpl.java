package com.management.houserent.service;

import com.management.houserent.dto.LeaseRequestDto;
import com.management.houserent.dto.LeaseResponseDto;
import com.management.houserent.exception.BadRequestException;
import com.management.houserent.exception.ResourceNotFoundException;
import com.management.houserent.model.*;
import com.management.houserent.repository.*;
import org.openpdf.text.pdf.PdfPTable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.openpdf.text.*;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.core.io.*;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;



@Service
public class LeaseServiceImpl implements com.management.houserent.service.LeaseService {

    private final LeaseRepository leaseRepo;
    private final RoomRepository roomRepo;
    private final TenantRepository tenantRepo;
    private final OwnerRepository ownerRepo;
    private final PaymentRepository paymentRepo;
    private final String leasesDir;

    public LeaseServiceImpl(LeaseRepository leaseRepo,
                            RoomRepository roomRepo,
                            TenantRepository tenantRepo,
                            OwnerRepository ownerRepo,
                            PaymentRepository paymentRepo,
                            @Value("${file.upload-leases-dir}")String leasesDir) {

        this.leaseRepo = leaseRepo;
        this.roomRepo = roomRepo;
        this.tenantRepo = tenantRepo;
        this.ownerRepo = ownerRepo;
        this.paymentRepo = paymentRepo;
        this.leasesDir = leasesDir;
    }

    @Override
    @Transactional
    public LeaseResponseDto createLease(String ownerEmail, LeaseRequestDto dto) {
        Owner owner = ownerRepo.findByUser_Email(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + ownerEmail));

        Room room = roomRepo.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + dto.getRoomId()));

        if (!room.getOwner().getId().equals(owner.getId())) {
            throw new IllegalArgumentException("Owner does not own this room");
        }

        Tenant tenant = tenantRepo.findById(dto.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + dto.getTenantId()));

        // Check if room already has an active lease
        leaseRepo.findByRoom_Id(room.getId()).ifPresent(existing -> {
            if (existing.getStatus() == LeaseStatus.ACTIVE || existing.getStatus() == LeaseStatus.PENDING) {
                throw new IllegalStateException("Room already has an active or pending lease");
            }
        });

        Lease lease = new Lease();
        lease.setRoom(room);
        lease.setOwner(owner);
        lease.setTenant(tenant);
        lease.setStartDate(dto.getStartDate());
        lease.setEndDate(dto.getEndDate());
        lease.setDepositAmount(dto.getDepositAmount());
        lease.setMonthlyRent(dto.getMonthlyRent());
        lease.setStatus(LeaseStatus.PENDING);
        lease.setCreatedAt(LocalDateTime.now());
        lease.setUpdatedAt(LocalDateTime.now());

        Lease saved = leaseRepo.save(lease);

        // Create a pending deposit payment record (tenant must pay)
        Payment deposit = new Payment();
        deposit.setLease(saved);
        deposit.setTenant(tenant);
        deposit.setAmount(dto.getDepositAmount());
        deposit.setPaymentType(PaymentType.DEPOSIT);
        deposit.setPaymentStatus(PaymentStatus.PENDING);
        deposit.setCreatedAt(LocalDateTime.now());
        deposit.setUpdatedAt(LocalDateTime.now());
        paymentRepo.save(deposit);

        return mapToDto(saved);
    }

    @Override
    @Transactional
    public LeaseResponseDto terminateLease(String ownerEmail, Long leaseId, String reason) {
        Lease lease = leaseRepo.findById(leaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Lease not found: " + leaseId));

        if (!lease.getOwner().getUser().getEmail().equalsIgnoreCase(ownerEmail)) {
            throw new IllegalArgumentException("Owner does not own this lease");
        }

        lease.setStatus(LeaseStatus.TERMINATED);
        lease.setUpdatedAt(LocalDateTime.now());

        // free the room
        Room room = lease.getRoom();
        room.setTenant(null);
        room.setAvailabilityStatus(Room.AvailabilityStatus.AVAILABLE);
        room.setUpdatedAt(LocalDateTime.now());
        roomRepo.save(room);

        Lease saved = leaseRepo.save(lease);
        return mapToDto(saved);
    }

    @Override
    public LeaseResponseDto getLease(Long leaseId, String requesterEmail) {
        Lease lease = leaseRepo.findById(leaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Lease not found: " + leaseId));
        // Optionally check requester has access (owner/tenant/admin) — do that in controller via PreAuthorize or check here
        return mapToDto(lease);
    }

    @Override
    public List<LeaseResponseDto> getLeasesForOwner(String ownerEmail) {
        Owner owner = ownerRepo.findByUser_Email(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + ownerEmail));
        return leaseRepo.findByOwner_Id(owner.getId()).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<LeaseResponseDto> getLeasesForTenant(String tenantEmail) {
        Tenant tenant = tenantRepo.findByEmail(tenantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + tenantEmail));
        return leaseRepo.findByTenant_Id(tenant.getId()).stream().map(this::mapToDto).collect(Collectors.toList());
    }


    @Override
    public void generateAgreementPdf(String requesterEmail, Long leaseId) throws Exception {
        Lease lease = leaseRepo.findById(leaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Lease not found: " + leaseId));

        // Authorization: only owner of lease OR tenant of lease OR admin can generate
        boolean allowed = false;
        if (lease.getOwner() != null && lease.getOwner().getUser() != null
                && lease.getOwner().getUser().getEmail().equalsIgnoreCase(requesterEmail)) allowed = true;
        if (lease.getTenant() != null && lease.getTenant().getEmail().equalsIgnoreCase(requesterEmail)) allowed = true;
        // admin check should be done earlier via PreAuthorize on controller

        if (!allowed) throw new AccessDeniedException("Not permitted");

        // create directories
        Path folder = Paths.get(leasesDir).toAbsolutePath().normalize();
        Files.createDirectories(folder);

        String filename = "lease_" + leaseId + "_" + System.currentTimeMillis() + ".pdf";
        Path target = folder.resolve(filename);

        try (OutputStream os = Files.newOutputStream(target, StandardOpenOption.CREATE_NEW)) {
            Document doc = new Document(PageSize.A4, 36, 36, 54, 36);
            PdfWriter.getInstance(doc, os);
            doc.open();

            // Header
            Font title = new Font(Font.HELVETICA, 16, Font.BOLD);
            Paragraph p = new Paragraph("LEASE AGREEMENT", title);
            p.setAlignment(Element.ALIGN_CENTER);
            doc.add(p);
            doc.add(Chunk.NEWLINE);

            DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;
            // Basic lease metadata
            PdfPTable table = new PdfPTable(2);
            table.setWidths(new float[]{1, 2});
            table.setWidthPercentage(100);
            table.addCell("Lease ID:");
            table.addCell(String.valueOf(lease.getId()));
            table.addCell("Start Date:");
            table.addCell(lease.getStartDate() != null ? lease.getStartDate().format(df) : "");
            table.addCell("End Date:");
            table.addCell(lease.getEndDate() != null ? lease.getEndDate().format(df) : "");
            table.addCell("Monthly Rent:");
            table.addCell(String.valueOf(lease.getMonthlyRent()));
            table.addCell("Deposit:");
            table.addCell(String.valueOf(lease.getDepositAmount()));
            doc.add(table);

            doc.add(Chunk.NEWLINE);
            // Parties
            Paragraph parties = new Paragraph();
            parties.add(new Paragraph("Owner: " + lease.getOwner().getName() + " (" + lease.getOwner().getEmail() + ")"));
            parties.add(new Paragraph("Tenant: " + lease.getTenant().getName() + " (" + lease.getTenant().getEmail() + ")"));
            doc.add(parties);

            doc.add(Chunk.NEWLINE);
            // Simple terms body — you can place a larger text or template here
            String terms = "This Lease Agreement is entered into between the Owner and Tenant named above. " +
                    "The Tenant agrees to pay the monthly rent and abide by the house rules. Deposit will be retained subject to terms.";
            doc.add(new Paragraph(terms));

            doc.add(Chunk.NEWLINE);
            doc.add(Chunk.NEWLINE);
            // Signatures
            PdfPTable sign = new PdfPTable(2);
            sign.setWidthPercentage(100);
            sign.addCell("Owner signature: _______________________");
            sign.addCell("Tenant signature: _______________________");
            doc.add(sign);

            doc.close();
        }

        // Save URL/path in DB (store relative path or endpoint)
        String publicUrl = "/api/leases/agreements/files/" + filename; // serve via controller
        lease.setAgreementUrl(publicUrl);
        leaseRepo.save(lease);
    }

    @Override
    public void uploadAgreementFile(String requesterEmail, Long leaseId, MultipartFile file) throws Exception {
        Lease lease = leaseRepo.findById(leaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Lease not found: "+leaseId));

        // Authorization check as above (owner/tenant)
        boolean allowed = lease.getOwner().getUser().getEmail().equalsIgnoreCase(requesterEmail)
                || lease.getTenant().getEmail().equalsIgnoreCase(requesterEmail);
        if (!allowed) throw new AccessDeniedException("Not permitted");

        if (file == null || file.isEmpty()) throw new BadRequestException("File required");
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        if (!original.toLowerCase().endsWith(".pdf")) throw new BadRequestException("Only PDF allowed");

        Path folder = Paths.get(leasesDir).toAbsolutePath().normalize();
        Files.createDirectories(folder);
        String filename = "lease_" + leaseId + "_" + System.currentTimeMillis() + ".pdf";
        Path target = folder.resolve(filename);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        lease.setAgreementUrl("/api/leases/agreements/files/" + filename);
        leaseRepo.save(lease);
    }

    @Override
    public Resource downloadAgreementByLease(Long leaseId, String requesterEmail) throws Exception {
        Lease lease = leaseRepo.findById(leaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Lease not found: " + leaseId));
        // authorization: owner/tenant/admin
        boolean allowed = lease.getOwner().getUser().getEmail().equalsIgnoreCase(requesterEmail)
                || lease.getTenant().getEmail().equalsIgnoreCase(requesterEmail);
        if (!allowed) throw new AccessDeniedException("Not permitted");

        String url = lease.getAgreementUrl();
        if (url == null) throw new ResourceNotFoundException("No agreement file");
        // url is /api/leases/agreements/files/{filename}
        String filename = Paths.get(url).getFileName().toString();
        Path file = Paths.get(leasesDir).resolve(filename).toAbsolutePath().normalize();
        if (!Files.exists(file)) throw new ResourceNotFoundException("File not found");

        Resource resource = new UrlResource(file.toUri());
        return resource;
    }

    private LeaseResponseDto mapToDto(Lease lease) {
        LeaseResponseDto dto = new LeaseResponseDto();
        dto.setId(lease.getId());
        dto.setRoomId(lease.getRoom().getId());
        dto.setOwnerId(lease.getOwner().getId());
        dto.setTenantId(lease.getTenant().getId());
        dto.setStartDate(lease.getStartDate());
        dto.setEndDate(lease.getEndDate());
        dto.setDepositAmount(lease.getDepositAmount());
        dto.setMonthlyRent(lease.getMonthlyRent());
        dto.setStatus(lease.getStatus().name());
        dto.setCreatedAt(lease.getCreatedAt());
        dto.setUpdatedAt(lease.getUpdatedAt());
        dto.setAgreementUrl(lease.getAgreementUrl());
        return dto;
    }
}
