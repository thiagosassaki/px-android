package com.mercadopago.android.px.internal.util;

import android.support.annotation.Nullable;
import com.mercadopago.android.px.model.Cause;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.exceptions.ApiException;
import java.util.List;

public final class EscUtil {

    private EscUtil() {
    }

    private static boolean hasValidParametersForESC(@Nullable final PaymentData paymentData,
        @Nullable final String paymentStatus, @Nullable final String paymentDetail) {
        return paymentData != null && paymentData.containsCardInfo()
            && !TextUtil.isEmpty(paymentStatus)
            && !TextUtil.isEmpty(paymentDetail);
    }

    public static boolean shouldDeleteEsc(@Nullable final PaymentData paymentData,
        @Nullable final String paymentStatus, @Nullable final String paymentDetail) {
        return hasValidParametersForESC(paymentData, paymentStatus, paymentDetail) &&
            !Payment.StatusCodes.STATUS_APPROVED.equals(paymentStatus);
    }

    public static boolean shouldStoreESC(@Nullable final PaymentData paymentData,
        @Nullable final String paymentStatus, @Nullable final String paymentDetail) {
        return hasValidParametersForESC(paymentData, paymentStatus, paymentDetail) &&
            Payment.StatusCodes.STATUS_APPROVED.equals(paymentStatus) &&
            !TextUtil.isEmpty(paymentData.getToken().getEsc());
    }

    public static boolean isInvalidEscPayment(@Nullable final PaymentData paymentData,
        @Nullable final String paymentStatus, @Nullable final String paymentDetail) {
        return hasValidParametersForESC(paymentData, paymentStatus, paymentDetail) &&
            Payment.StatusCodes.STATUS_REJECTED.equals(paymentStatus) &&
            Payment.StatusDetail.STATUS_DETAIL_INVALID_ESC.equals(paymentDetail);
    }

    public static boolean isErrorInvalidPaymentWithEsc(final MercadoPagoError error, final PaymentData paymentData) {
        if (error.isApiException() && error.getApiException().getStatus() == ApiUtil.StatusCodes.BAD_REQUEST) {
            final List<Cause> causes = error.getApiException().getCause();
            if (causes != null && paymentData.containsCardInfo()) {
                boolean isInvalidEsc = false;
                for (final Cause cause : causes) {
                    isInvalidEsc |= ApiException.ErrorCodes.INVALID_PAYMENT_WITH_ESC.equals(cause.getCode());
                }
                return isInvalidEsc;
            }
        }
        return false;
    }
}
