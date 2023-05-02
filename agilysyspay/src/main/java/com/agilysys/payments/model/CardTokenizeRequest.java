package com.agilysys.payments.model;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//




import java.io.Serializable;

public class CardTokenizeRequest implements Serializable {
    private static final long serialVersionUID = -1859042643349744670L;
    private String cardholderName;
    private String cardNumber;
    private String expirationMonth;
    private String expirationYear;
    private String cvv;
    private String postalCode;
    private String doVerify;
    private String customerId;
    private String browserInfo;
    private BillingAddress billingAddress;
    private String googleCaptcha;
    private boolean enableCaptcha;
    private String token;
    private String dateTimeZone;

    public CardTokenizeRequest() {
    }

    public String getCardholderName() {
        return this.cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpirationMonth() {
        return this.expirationMonth;
    }

    public void setExpirationMonth(String expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public String getExpirationYear() {
        return this.expirationYear;
    }

    public void setExpirationYear(String expirationYear) {
        this.expirationYear = expirationYear;
    }

    public String getCvv() {
        return this.cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getDoVerify() {
        return this.doVerify;
    }

    public void setDoVerify(String doVerify) {
        this.doVerify = doVerify;
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getBrowserInfo() {
        return this.browserInfo;
    }

    public void setBrowserInfo(String browserInfo) {
        this.browserInfo = browserInfo;
    }

    public BillingAddress getBillingAddress() {
        return this.billingAddress;
    }

    public void setBillingAddress(BillingAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getGoogleCaptcha() {
        return this.googleCaptcha;
    }

    public void setGoogleCaptcha(String googleCaptcha) {
        this.googleCaptcha = googleCaptcha;
    }

    public boolean isEnableCaptcha() {
        return this.enableCaptcha;
    }

    public void setEnableCaptcha(boolean enableCaptcha) {
        this.enableCaptcha = enableCaptcha;
    }

    public String getDateTimeZone() {
        return this.dateTimeZone;
    }

    public void setDateTimeZone(String dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }
}

