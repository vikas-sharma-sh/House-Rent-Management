package com.management.houserent.controller;

import com.management.houserent.dto.PaymentRequestDto;
import com.management.houserent.dto.PaymentResponseDto;
import com.management.houserent.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }



    @PostMapping("/create-order")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<Map<String, Object>> createOrder(Authentication auth,@Valid @RequestBody PaymentRequestDto dto) throws Exception {
        return ResponseEntity.ok(paymentService.createOrderForLease(auth.getName(), dto));
    }

    @PostMapping("/verify")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<PaymentResponseDto> verify(@RequestBody Map<String, String> body) throws Exception {
        String orderId = body.get("razorpayOrderId");
        String paymentId = body.get("razorpayPaymentId");
        String signature = body.get("razorpaySignature");
        return ResponseEntity.ok(paymentService.verifyPaymentByClient(orderId, paymentId, signature));
    }

    // Webhook from Razorpay — no authentication; secured by signature verification
    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestHeader("X-Razorpay-Signature") String signature,
                                          @RequestBody String payload) throws Exception {
        paymentService.handleRazorpayWebhook(payload, signature);
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/lease/{leaseId}")
    @PreAuthorize("hasAnyRole('OWNER','TENANT','ADMIN')")
    public ResponseEntity<List<PaymentResponseDto>> paymentsForLease(@PathVariable Long leaseId) {
        return ResponseEntity.ok(paymentService.getPaymentsForLease(leaseId));
    }

    @GetMapping("/tenant/me")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<PaymentResponseDto>> paymentsForTenant(Authentication auth) {
        return ResponseEntity.ok(paymentService.getPaymentsForTenant(auth.getName()));
    }
}
