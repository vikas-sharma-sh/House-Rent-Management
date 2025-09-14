package com.management.houserent.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentRequestDto {


    @NotNull(message = "Lease ID is required")
    private Long leaseId;


    @NotNull(message = "Amount is Required ")
    @Min(value = 1 , message = "Amount Must Be Positive")
    private Double amount;

    @NotBlank(message ="Payment Type Is Required" )
    private String paymentType; // DEPOSIT or RENT

    public PaymentRequestDto() {}

    public Long getLeaseId() {
        return leaseId;
    }

    public void setLeaseId(Long leaseId) {
        this.leaseId = leaseId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
}
