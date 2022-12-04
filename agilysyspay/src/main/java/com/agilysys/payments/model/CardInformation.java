package com.agilysys.payments.model;

public class CardInformation {

    private String cardIssuer;
    private String accountNumberMasked;
    private String expirationYearMonth;
    private String cardHolderName;
    private String cvv;
    private String postalCode;
    private String cardIssuerExtension;
    private String entryMode;
    private String cardType;

    public String getCardIssuer() {
        return cardIssuer;
    }

    public void setCardIssuer(String cardIssuer) {
        this.cardIssuer = cardIssuer;
    }

    public String getAccountNumberMasked() {
        return accountNumberMasked;
    }

    public void setAccountNumberMasked(String accountNumberMasked) {
        this.accountNumberMasked = accountNumberMasked;
    }

    public String getExpirationYearMonth() {
        return expirationYearMonth;
    }

    public void setExpirationYearMonth(String expirationYearMonth) {
        this.expirationYearMonth = expirationYearMonth;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCardIssuerExtension() {
        return cardIssuerExtension;
    }

    public void setCardIssuerExtension(String cardIssuerExtension) {
        this.cardIssuerExtension = cardIssuerExtension;
    }

    public String getEntryMode() {
        return entryMode;
    }

    public void setEntryMode(String entryMode) {
        this.entryMode = entryMode;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}
