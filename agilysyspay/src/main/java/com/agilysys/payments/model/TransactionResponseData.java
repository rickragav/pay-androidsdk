package com.agilysys.payments.model;

import java.math.BigDecimal;

public class TransactionResponseData {

    private BigDecimal subTotalAmount;
    private BigDecimal tipAmount;
    private BigDecimal totalAmount;
    private BigDecimal authorizedAmount;

    public BigDecimal getSubTotalAmount() {
        return subTotalAmount;
    }

    public void setSubTotalAmount(BigDecimal subTotalAmount) {
        this.subTotalAmount = subTotalAmount;
    }

    public BigDecimal getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(BigDecimal tipAmount) {
        this.tipAmount = tipAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getAuthorizedAmount() {
        return authorizedAmount;
    }

    public void setAuthorizedAmount(BigDecimal authorizedAmount) {
        this.authorizedAmount = authorizedAmount;
    }
}
