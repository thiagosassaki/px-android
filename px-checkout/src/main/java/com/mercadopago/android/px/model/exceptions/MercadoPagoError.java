package com.mercadopago.android.px.model.exceptions;

import java.io.Serializable;

public class MercadoPagoError implements Serializable {

    private String message;
    private String errorDetail;
    private String requestOrigin;
    private ApiException apiException;
    private final boolean recoverable;

    public MercadoPagoError(final String message, final boolean recoverable) {
        this.message = message;
        this.recoverable = recoverable;
    }

    public MercadoPagoError(String message, String detail, boolean recoverable) {
        this.message = message;
        errorDetail = detail;
        this.recoverable = recoverable;
    }

    public MercadoPagoError(ApiException apiException, String requestOrigin) {
        this.apiException = apiException;
        this.requestOrigin = requestOrigin;
        recoverable = apiException != null && apiException.isRecoverable();
    }

    public ApiException getApiException() {
        return apiException;
    }

    public boolean isRecoverable() {
        return recoverable;
    }

    public String getMessage() {
        return message;
    }

    public String getRequestOrigin() {
        return requestOrigin;
    }

    public String getErrorDetail() {
        return errorDetail == null ? "" : errorDetail;
    }

    public boolean isApiException() {
        return apiException != null;
    }

    @Override
    public String toString() {
        return "MercadoPagoError{" +
            "message='" + message + '\'' +
            ", errorDetail='" + errorDetail + '\'' +
            ", requestOrigin='" + requestOrigin + '\'' +
            ", apiException=" + apiException +
            ", recoverable=" + recoverable +
            '}';
    }
}
