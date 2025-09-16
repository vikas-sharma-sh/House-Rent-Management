package com.management.houserent;

import com.management.houserent.listener.PaymentSuccessListener;
import com.management.houserent.model.Lease;
import com.management.houserent.model.Payment;
import com.management.houserent.model.Tenant;
import com.management.houserent.model.PaymentStatus;
import com.management.houserent.model.PaymentType;
import com.management.houserent.service.MailService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PaymentEmailTest {

    @Test
    void shouldSendReceiptEmail() throws Exception {
        // Mock MailService
        MailService mailService = Mockito.mock(MailService.class);
        PaymentSuccessListener listener = new PaymentSuccessListener(mailService);

        // Build fake data
        Tenant tenant = new Tenant();
        tenant.setName("John Doe");
        tenant.setEmail("john@example.com");

        Lease lease = new Lease();
        lease.setId(1L);

        Payment payment = new Payment();
        payment.setAmount(5000.0);
        payment.setPaymentType(PaymentType.DEPOSIT);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setTenant(tenant);
        payment.setLease(lease);

        // Call
        listener.sendReceiptEmail(payment);

        // Verify email called
        verify(mailService, times(1)).sendMail(eq("john@example.com"),
                contains("Payment Receipt"),
                contains("Payment Successful"),
                any());
    }
}
