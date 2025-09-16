package com.management.houserent.listener;

import com.management.houserent.model.Payment;
import com.management.houserent.service.MailService;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class PaymentSuccessListener {

    private final MailService mailService;

    public PaymentSuccessListener(MailService mailService) {
        this.mailService = mailService;
    }


    public void sendReceiptEmail(Payment payment) {
        try {
            String to = payment.getTenant().getEmail();
            String subject = "Payment Receipt - House Rent Management";
            String body = "<h2>Payment Successful</h2>" +
                    "<p>Dear " + payment.getTenant().getName() + ",</p>" +
                    "<p>Your payment of ₹" + payment.getAmount() + " for Lease #" + payment.getLease().getId() + " has been received.</p>" +
                    "<p>Type: " + payment.getPaymentType() + " | Status: " + payment.getPaymentStatus() + "</p>";

            File attachment = null;
            if ("DEPOSIT".equalsIgnoreCase(payment.getPaymentType().name())) {
                String agreementUrl = payment.getLease().getAgreementUrl();
                if (agreementUrl != null && !agreementUrl.isBlank()) {
                    String filename = agreementUrl.substring(agreementUrl.lastIndexOf('/') + 1);
                    File leasesDir = new File("uploads/leases");
                    File possible = new File(leasesDir, filename);
                    if (possible.exists()) {
                        attachment = possible;
                    }
                }
            }

            mailService.sendMail(to, subject, body, attachment);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
