package com.management.houserent.dto;

import java.time.LocalDate;

public class LeaseUpdateRequest {
    private LocalDate endDate;       // for renew/extend
    private String agreementUrl;     // new doc

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getAgreementUrl() {
        return agreementUrl;
    }

    public void setAgreementUrl(String agreementUrl) {
        this.agreementUrl = agreementUrl;
    }
}
