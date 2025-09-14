package com.management.houserent.service;

import com.management.houserent.dto.PaymentRequestDto;
import com.management.houserent.dto.PaymentResponseDto;

import com.management.houserent.exception.ResourceNotFoundException;
import com.management.houserent.model.*;
import com.management.houserent.repository.*;
import com.management.houserent.util.RazorpaySignatureUtil;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepo;
    private final LeaseRepository leaseRepo;
    private final TenantRepository tenantRepo;
    private final RoomRepository roomRepo;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    @Value("${razorpay.webhook-secret}")
    private String razorpayWebhookSecret;


    public PaymentServiceImpl(PaymentRepository paymentRepo,
                              LeaseRepository leaseRepo,
                              TenantRepository tenantRepo,
                              RoomRepository roomRepo, ApplicationEventPublisher eventPublisher) {
        this.paymentRepo = paymentRepo;
        this.leaseRepo = leaseRepo;
        this.tenantRepo = tenantRepo;
        this.roomRepo = roomRepo;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Map<String, Object> createOrderForLease(String tenantEmail, PaymentRequestDto dto) throws Exception {
        Tenant tenant = tenantRepo.findByEmail(tenantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + tenantEmail));

        Lease lease = leaseRepo.findById(dto.getLeaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Lease not found: " + dto.getLeaseId()));

        // find an existing pending deposit payment (preferable)
        Optional<Payment> existingPending = paymentRepo.findFirstByLease_IdAndPaymentTypeAndPaymentStatus(
                lease.getId(), PaymentType.DEPOSIT, PaymentStatus.PENDING);

        Payment payment;
        if (existingPending.isPresent()) {
            payment = existingPending.get();
        } else {
            // create payment record
            payment = new Payment();
            payment.setLease(lease);
            payment.setTenant(tenant);
            payment.setAmount(dto.getAmount());
            payment.setPaymentType(PaymentType.valueOf(dto.getPaymentType().toUpperCase()));
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setCreatedAt(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());
            payment = paymentRepo.save(payment);
        }

        // Create Razorpay order
        RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        int amountPaise = (int) Math.round(dto.getAmount() * 100);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "lease_" + lease.getId() + "_" + System.currentTimeMillis());
        orderRequest.put("payment_capture", 1);

        Order order = client.orders.create(orderRequest);

        // attach razorpay order id to payment record
        payment.setRazorpayOrderId(order.get("id"));
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepo.save(payment);

        Map<String, Object> resp = new HashMap<>();
        resp.put("orderId", order.get("id"));
        resp.put("razorpayKeyId", razorpayKeyId);
        resp.put("amount", amountPaise);
        resp.put("currency", "INR");
        resp.put("leaseId", lease.getId());
        resp.put("tenantEmail", tenantEmail);

        return resp;
    }

    @Override
    @Transactional
    public PaymentResponseDto verifyPaymentByClient(String orderId, String paymentId, String signature) throws Exception {
        String payload = orderId + "|" + paymentId;
        String expected = RazorpaySignatureUtil.hmacSha256Hex(payload, razorpayKeySecret);
        if (!RazorpaySignatureUtil.secureCompare(expected, signature)) {
            throw new IllegalArgumentException("Invalid signature");
        }

        Payment payment = paymentRepo.findByRazorpayOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for orderId: " + orderId));

        // optional: fetch actual payment details from Razorpay for extra safety (omitted here but recommended)
        payment.setRazorpayPaymentId(paymentId);
        payment.setRazorpaySignature(signature);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepo.save(payment);

        // Activate lease if deposit
        Lease lease = payment.getLease();
        if (payment.getPaymentType() == PaymentType.DEPOSIT && lease.getStatus() == LeaseStatus.PENDING) {
            lease.setStatus(LeaseStatus.ACTIVE);
            lease.setUpdatedAt(LocalDateTime.now());
            // mark room occupied
            Room room = lease.getRoom();
            room.setTenant(lease.getTenant());
            room.setAvailabilityStatus(Room.AvailabilityStatus.OCCUPIED);
            room.setUpdatedAt(LocalDateTime.now());
            roomRepo.save(room);
            leaseRepo.save(lease);
        }


        return mapToDto(payment);
    }

    @Override
    @Transactional
    public void handleRazorpayWebhook(String payload, String signature) throws Exception {
        String expected = RazorpaySignatureUtil.hmacSha256Hex(payload, razorpayWebhookSecret);
        if (!RazorpaySignatureUtil.secureCompare(expected, signature)) {
            throw new IllegalArgumentException("Invalid webhook signature");
        }

        JSONObject json = new JSONObject(payload);
        String event = json.optString("event", "");

        JSONObject paymentObj = json.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");
        String orderId = paymentObj.optString("order_id", null);
        String rpPaymentId = paymentObj.optString("id", null);

        Optional<Payment> maybePayment = paymentRepo.findByRazorpayOrderId(orderId);
        if (maybePayment.isEmpty()) {
            // Could log unknown payment for reconciliation
            return;
        }

        Payment payment = maybePayment.get();
        if ("payment.captured".equals(event) || "payment.authorized".equals(event)) {
            if (payment.getPaymentStatus() != PaymentStatus.SUCCESS) {
                payment.setPaymentStatus(PaymentStatus.SUCCESS);
                payment.setRazorpayPaymentId(rpPaymentId);
                payment.setUpdatedAt(LocalDateTime.now());
                paymentRepo.save(payment);

                Lease lease = payment.getLease();
                if (payment.getPaymentType() == PaymentType.DEPOSIT && lease.getStatus() == LeaseStatus.PENDING) {
                    lease.setStatus(LeaseStatus.ACTIVE);
                    lease.setUpdatedAt(LocalDateTime.now());
                    Room room = lease.getRoom();
                    room.setTenant(lease.getTenant());
                    room.setAvailabilityStatus(Room.AvailabilityStatus.OCCUPIED);
                    room.setUpdatedAt(LocalDateTime.now());
                    roomRepo.save(room);
                    leaseRepo.save(lease);
                }
            }
        } else if ("payment.failed".equals(event)) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepo.save(payment);
        }
        // handle refund.* events later
    }

    @Override
    public List<PaymentResponseDto> getPaymentsForLease(Long leaseId) {
        return paymentRepo.findByLease_Id(leaseId).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponseDto> getPaymentsForTenant(String tenantEmail) {
        Tenant tenant = tenantRepo.findByEmail(tenantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + tenantEmail));
        return paymentRepo.findByTenant_Id(tenant.getId()).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private PaymentResponseDto mapToDto(Payment p) {
        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setId(p.getId());
        dto.setLeaseId(p.getLease().getId());
        dto.setTenantId(p.getTenant().getId());
        dto.setAmount(p.getAmount());
        dto.setPaymentType(p.getPaymentType().name());
        dto.setPaymentStatus(p.getPaymentStatus().name());
        dto.setRazorpayOrderId(p.getRazorpayOrderId());
        dto.setRazorpayPaymentId(p.getRazorpayPaymentId());
        dto.setRazorpaySignature(p.getRazorpaySignature());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());
        return dto;
    }
}
