package com.agilysys.payments.model;

public class Response{
    public GatewayResponseData gatewayResponseData;
    public int code;

    public GatewayResponseData getGatewayResponseData() {
        return gatewayResponseData;
    }

    public void setGatewayResponseData(GatewayResponseData gatewayResponseData) {
        this.gatewayResponseData = gatewayResponseData;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String reason;
    public String message;
}
