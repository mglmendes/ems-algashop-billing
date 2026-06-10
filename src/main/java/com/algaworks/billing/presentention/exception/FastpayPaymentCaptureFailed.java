package com.algaworks.billing.presentention.exception;


public class FastpayPaymentCaptureFailed extends BadGatewayException {
    public FastpayPaymentCaptureFailed() {
    }

    public FastpayPaymentCaptureFailed(String message, Throwable cause) {
        super(message, cause);
    }

    public FastpayPaymentCaptureFailed(String message) {
        super(message);
    }
}