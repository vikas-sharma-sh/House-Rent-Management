
package com.management.houserent.repository;

import com.management.houserent.model.Payment;
import com.management.houserent.model.PaymentType;
import com.management.houserent.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByLease_Id(Long leaseId);
    List<Payment> findByTenant_Id(Long tenantId);
    Optional<Payment> findByRazorpayOrderId(String orderId);
    Optional<Payment> findByRazorpayPaymentId(String paymentId);
    Optional<Payment> findFirstByLease_IdAndPaymentTypeAndPaymentStatus(Long leaseId, PaymentType type, PaymentStatus status);
}
