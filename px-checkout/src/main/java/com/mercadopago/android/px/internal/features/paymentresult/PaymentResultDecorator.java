package com.mercadopago.android.px.internal.features.paymentresult;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.IPayment;
import com.mercadopago.android.px.model.Payment;

public final class PaymentResultDecorator {

    private PaymentResultDecorator() {

    }

    public static boolean isSuccessBackground(@NonNull final IPayment payment) {
        return isSuccessBackground(payment.getPaymentStatus(), payment.getPaymentStatusDetail());
    }

    public static boolean isSuccessBackground(@NonNull final String status, @NonNull final String statusDetail) {
        return (status.equals(Payment.StatusCodes.STATUS_APPROVED) ||
            ((status.equals(Payment.StatusCodes.STATUS_IN_PROCESS) ||
                status.equals(Payment.StatusCodes.STATUS_PENDING)) &&
                statusDetail
                    .equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT)));
    }

    public static boolean isErrorNonRecoverableBackground(@NonNull final IPayment payment) {
        return isErrorNonRecoverableBackground(payment.getPaymentStatus(), payment.getPaymentStatusDetail());
    }

    public static boolean isErrorNonRecoverableBackground(@NonNull final String status,
        @NonNull final String statusDetail) {
        return Payment.StatusCodes.STATUS_REJECTED.equals(status) &&
            (Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_OTHER_REASON.equals(statusDetail) ||
                Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_PLUGIN_PM.equals(statusDetail) ||
                Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_BY_BANK.equals(statusDetail) ||
                Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_INSUFFICIENT_DATA.equals(statusDetail) ||
                Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_DUPLICATED_PAYMENT.equals(statusDetail) ||
                Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_MAX_ATTEMPTS.equals(statusDetail) ||
                Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_HIGH_RISK.equals(statusDetail) ||
                Payment.StatusDetail.STATUS_DETAIL_REJECTED_HIGH_RISK.equals(statusDetail));
    }

    public static boolean isPendingOrErrorRecoverableBackground(@NonNull final IPayment payment) {
        return isPendingOrErrorRecoverableBackground(payment.getPaymentStatus(), payment.getPaymentStatusDetail());
    }

    public static boolean isPendingOrErrorRecoverableBackground(@NonNull final String status,
        @NonNull final String statusDetail) {
        return isPendingWarningBadge(status, statusDetail) || isErrorRecoverableBadge(status, statusDetail);
    }

    public static boolean isPendingWarningBadge(@NonNull final IPayment payment) {
        return isPendingWarningBadge(payment.getPaymentStatus(), payment.getPaymentStatusDetail());
    }

    public static boolean isPendingWarningBadge(@NonNull final String status,
        @NonNull final String statusDetail) {
        return (status.equals(Payment.StatusCodes.STATUS_PENDING) ||
            status.equals(Payment.StatusCodes.STATUS_IN_PROCESS)) &&
            (statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_CONTINGENCY) ||
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_REVIEW_MANUAL));
    }

    public static boolean isErrorRecoverableBadge(@NonNull final IPayment payment) {
        return isErrorRecoverableBadge(payment.getPaymentStatus(), payment.getPaymentStatusDetail());
    }

    public static boolean isErrorRecoverableBadge(@NonNull final String status,
        @NonNull final String statusDetail) {
        return status.equals(Payment.StatusCodes.STATUS_REJECTED) &&
            (statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_INVALID_ESC) ||
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE) ||
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_CARD_NUMBER) ||
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_OTHER) ||
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE) ||
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE) ||
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED) ||
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT));
    }

    public static boolean isErrorNonRecoverableBadge(@NonNull final String status,
        @NonNull final String statusDetail) {
        return status.equals(Payment.StatusCodes.STATUS_REJECTED) && (
            statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_OTHER_REASON) ||
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_PLUGIN_PM) ||
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_BY_BANK) ||
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_INSUFFICIENT_DATA) ||
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_DUPLICATED_PAYMENT) ||
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_MAX_ATTEMPTS) ||
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_HIGH_RISK));
    }

    public static boolean isCheckBagde(@NonNull final IPayment payment) {
        return isCheckBagde(payment.getPaymentStatus());
    }

    public static boolean isCheckBagde(@NonNull final String status) {
        return status.equals(Payment.StatusCodes.STATUS_APPROVED);
    }

    public static boolean isPendingSuccessBadge(@NonNull final IPayment payment) {
        return isPendingSuccessBadge(payment.getPaymentStatus(), payment.getPaymentStatusDetail());
    }

    public static boolean isPendingSuccessBadge(@NonNull final String status, @NonNull final String statusDetail) {
        return (status.equals(Payment.StatusCodes.STATUS_PENDING) ||
            status.equals(Payment.StatusCodes.STATUS_IN_PROCESS)) &&
            statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT);
    }
}
