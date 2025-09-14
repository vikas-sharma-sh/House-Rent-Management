package com.management.houserent.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class LeaseCreateRequest {
    @NotNull private Long roomId;
    @NotNull private Long tenantId;
    @NotNull private LocalDate startDate;
    @NotNull @Future private LocalDate endDate;
    @NotNull private Double monthlyRent;
    private String agreementUrl; // optional upload integration later

    public @NotNull Long getRoomId() {
        return roomId;
    }

    public void setRoomId(@NotNull Long roomId) {
        this.roomId = roomId;
    }

    public @NotNull Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(@NotNull Long tenantId) {
        this.tenantId = tenantId;
    }

    public @NotNull LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(@NotNull LocalDate startDate) {
        this.startDate = startDate;
    }

    public @NotNull @Future LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(@NotNull @Future LocalDate endDate) {
        this.endDate = endDate;
    }

    public @NotNull Double getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(@NotNull Double monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public String getAgreementUrl() {
        return agreementUrl;
    }

    public void setAgreementUrl(String agreementUrl) {
        this.agreementUrl = agreementUrl;
    }
}
