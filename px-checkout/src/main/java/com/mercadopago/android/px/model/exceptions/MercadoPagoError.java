package com.mercadopago.android.px.model.exceptions;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.ScreenViewEvent;
import com.mercadopago.android.px.tracking.internal.utils.TrackingUtil;
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
        if (message == null && getApiException() != null && !TextUtil.isEmpty(getApiException().getMessage())) {
            return getApiException().getMessage();
        }
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

    public boolean isPaymentProcessing() {
        return getApiException() != null
            && getApiException().getStatus() == ApiUtil.StatusCodes.PROCESSING;
    }

    public boolean isInternalServerError() {
        return getApiException() != null
            && String.valueOf(getApiException().getStatus())
            .startsWith(ApiUtil.StatusCodes.INTERNAL_SERVER_ERROR_FIRST_DIGIT);
    }

    public boolean isBadRequestError() {
        return getApiException() != null
            && (getApiException().getStatus() == ApiUtil.StatusCodes.BAD_REQUEST);
    }

    public boolean isNoConnectivityError() {
        return getApiException() != null
            && (getApiException().getStatus() == ApiUtil.StatusCodes.NO_CONNECTIVITY_ERROR);
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

    public ScreenViewEvent.Builder getErrorEvent(@NonNull final ScreenViewEvent.Builder builder) {

        if (getApiException() != null) {

            builder.addProperty(TrackingUtil.PROPERTY_ERROR_STATUS, String.valueOf(apiException.getStatus()));

            if (apiException.getCause() != null && !apiException.getCause().isEmpty() &&
                apiException.getCause().get(0).getCode() != null) {
                builder.addProperty(TrackingUtil.PROPERTY_ERROR_CODE,
                    String.valueOf(apiException.getCause().get(0).getCode()));
            }
            if (!TextUtils.isEmpty(apiException.getMessage())) {
                builder.addProperty(TrackingUtil.PROPERTY_ERROR_MESSAGE, apiException.getMessage());
            }
        }

        if (getRequestOrigin() != null && !getRequestOrigin().isEmpty()) {
            builder.addProperty(TrackingUtil.PROPERTY_ERROR_REQUEST, getRequestOrigin());
        }
        return builder;
    }
}
