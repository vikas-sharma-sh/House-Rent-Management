package com.management.houserent.service;

import com.management.houserent.dto.PaymentRequestDto;
import com.management.houserent.dto.PaymentResponseDto;

import java.util.List;
import java.util.Map;

public interface PaymentService {
    // returns map that includes razorpay orderId, keyId, amount (paise), currency
    Map<String, Object> createOrderForLease(String tenantEmail, PaymentRequestDto dto) throws Exception;

    // verify called by client after checkout (orderId|paymentId|signature)
    PaymentResponseDto verifyPaymentByClient(String orderId, String paymentId, String signature) throws Exception;

    // webhook handler
    void handleRazorpayWebhook(String payload, String signature) throws Exception;

    List<PaymentResponseDto> getPaymentsForLease(Long leaseId);
    List<PaymentResponseDto> getPaymentsForTenant(String tenantEmail);
}
