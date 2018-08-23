package com.mercadopago.android.px.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.Serializable;

public class PaymentResult implements Serializable {

    public static final String SELECT_OTHER_PAYMENT_METHOD = "select_other_payment_method";
    public static final String RECOVER_PAYMENT = "recover_payment";

    private final PaymentData paymentData;
    private final Long paymentId;
    private final String paymentStatus;
    private final String paymentStatusDetail;
    private final String statementDescription;

    /* default */ PaymentResult(final Builder builder) {
        paymentData = builder.paymentData;
        paymentId = builder.paymentId;
        paymentStatus = builder.paymentStatus;
        paymentStatusDetail = builder.paymentStatusDetail;
        statementDescription = builder.statementDescription;
    }

    public PaymentData getPaymentData() {
        return paymentData;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public boolean isStatusApproved() {
        return Payment.StatusCodes.STATUS_APPROVED.equals(paymentStatus);
    }

    public boolean isStatusRejected() {
        return Payment.StatusCodes.STATUS_REJECTED.equals(paymentStatus);
    }

    public boolean isStatusPending() {
        return Payment.StatusCodes.STATUS_PENDING.equals(paymentStatus);
    }

    public boolean isStatusInProcess() {
        return Payment.StatusCodes.STATUS_IN_PROCESS.equals(paymentStatus);
    }

    public String getPaymentStatusDetail() {
        return paymentStatusDetail;
    }

    public String getStatementDescription() {
        return statementDescription;
    }

    public boolean isCallForAuthorize() {
        return Payment.StatusCodes.STATUS_REJECTED.equals(getPaymentStatus()) &&
            Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE.equals(getPaymentStatusDetail());
    }

    public boolean isRejected() {
        return Payment.StatusCodes.STATUS_REJECTED.equals(getPaymentStatus());
    }

    public boolean isInstructions() {
        return (Payment.StatusCodes.STATUS_PENDING.equals(getPaymentStatus()) ||
            Payment.StatusCodes.STATUS_IN_PROCESS.equals(getPaymentStatus())) &&
            Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT.equals(getPaymentStatusDetail());
    }

    public boolean isPending() {
        return getPaymentStatus().equals(Payment.StatusCodes.STATUS_PENDING) ||
            getPaymentStatus().equals(Payment.StatusCodes.STATUS_IN_PROCESS);
    }

    public static final class Builder {

        /* default */ PaymentData paymentData;
        /* default */ Long paymentId;
        /* default */ String paymentStatus;
        /* default */ String paymentStatusDetail;
        /* default */ String statementDescription;

        public Builder setPaymentData(@NonNull final PaymentData paymentData) {
            this.paymentData = paymentData;
            return this;
        }

        public Builder setPaymentId(@NonNull final Long paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder setPaymentStatus(@NonNull final String paymentStatus) {
            this.paymentStatus = paymentStatus;
            return this;
        }

        public Builder setPaymentStatusDetail(@NonNull final String statusDetail) {
            paymentStatusDetail = statusDetail;
            return this;
        }

        public Builder setStatementDescription(@Nullable final String statementDescription) {
            this.statementDescription = statementDescription;
            return this;
        }

        public PaymentResult build() {
            return new PaymentResult(this);
        }
    }
}
