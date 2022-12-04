package com.agilysys.payments.model;

public class TransactionReferenceData {

    private String transactionId;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenExpirationDate() {
        return tokenExpirationDate;
    }

    public void setTokenExpirationDate(String tokenExpirationDate) {
        this.tokenExpirationDate = tokenExpirationDate;
    }

    public String getTransactionFollowOnData() {
        return transactionFollowOnData;
    }

    public void setTransactionFollowOnData(String transactionFollowOnData) {
        this.transactionFollowOnData = transactionFollowOnData;
    }

    public String getComplianceDataValue() {
        return complianceDataValue;
    }

    public void setComplianceDataValue(String complianceDataValue) {
        this.complianceDataValue = complianceDataValue;
    }

    private String token;
    private String tokenExpirationDate;
    private String transactionFollowOnData;
    private String complianceDataValue;
}
