package com.agilysys.payments.model;

public class GatewayResponse {
    private TransactionReferenceData transactionReferenceData;
    private TransactionResponseData transactionResponseData;
    private CardInformation cardInfo;

    public TransactionReferenceData getTransactionReferenceData() {
        return transactionReferenceData;
    }

    public void setTransactionReferenceData(TransactionReferenceData transactionReferenceData) {
        this.transactionReferenceData = transactionReferenceData;
    }

    public TransactionResponseData getTransactionResponseData() {
        return transactionResponseData;
    }

    public void setTransactionResponseData(TransactionResponseData transactionResponseData) {
        this.transactionResponseData = transactionResponseData;
    }

    public GatewayResponseData getGatewayResponseData() {
        return gatewayResponseData;
    }

    public void setGatewayResponseData(GatewayResponseData gatewayResponseData) {
        this.gatewayResponseData = gatewayResponseData;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    private GatewayResponseData gatewayResponseData;
    private String redirectUrl;
}
