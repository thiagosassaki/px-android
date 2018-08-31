package com.mercadopago.android.px.internal.features.paymentresult.props;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.model.Instruction;
import com.mercadopago.android.px.model.PaymentData;
import java.math.BigDecimal;

public class PaymentResultBodyProps {

    public final String status;
    public final String statusDetail;
    public final Instruction instruction;
    public final PaymentData paymentData;
    public final String processingMode;
    public final String disclaimer;
    public final Long paymentId;
    public final String currencyId;
    public final BigDecimal amount;
    public PaymentResultScreenConfiguration paymentResultScreenConfiguration;

    public PaymentResultBodyProps(@NonNull final Builder builder) {
        status = builder.status;
        statusDetail = builder.statusDetail;
        instruction = builder.instruction;
        paymentData = builder.paymentData;
        disclaimer = builder.disclaimer;
        processingMode = builder.processingMode;
        paymentId = builder.paymentId;
        currencyId = builder.currencyId;
        amount = builder.amount;
        paymentResultScreenConfiguration = builder.paymentResultScreenConfiguration;
    }

    public Builder toBuilder() {
        return new Builder(paymentResultScreenConfiguration)
            .setStatus(status)
            .setCurrencyId(currencyId)
            .setAmount(amount)
            .setStatusDetail(statusDetail)
            .setInstruction(instruction)
            .setPaymentData(paymentData)
            .setDisclaimer(disclaimer)
            .setProcessingMode(processingMode)
            .setPaymentId(paymentId);
    }

    public static class Builder {

        public String status;
        public String statusDetail;
        public Instruction instruction;
        public PaymentData paymentData;
        public String disclaimer;
        public String processingMode;
        public Long paymentId;
        public String currencyId;
        public BigDecimal amount;
        public PaymentResultScreenConfiguration paymentResultScreenConfiguration;

        public Builder(@NonNull final PaymentResultScreenConfiguration paymentResultScreenConfiguration) {
            this.paymentResultScreenConfiguration = paymentResultScreenConfiguration;
        }

        public Builder setStatus(@NonNull final String status) {
            this.status = status;
            return this;
        }

        public Builder setStatusDetail(@NonNull final String statusDetail) {
            this.statusDetail = statusDetail;
            return this;
        }

        public Builder setInstruction(final Instruction instruction) {
            this.instruction = instruction;
            return this;
        }

        public Builder setPaymentData(final PaymentData paymentData) {
            this.paymentData = paymentData;
            return this;
        }

        public Builder setDisclaimer(String disclaimer) {
            this.disclaimer = disclaimer;
            return this;
        }

        public Builder setProcessingMode(final String processingMode) {
            this.processingMode = processingMode;
            return this;
        }

        public Builder setPaymentId(Long paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder setCurrencyId(final String currencyId) {
            this.currencyId = currencyId;
            return this;
        }

        public Builder setAmount(final BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public PaymentResultBodyProps build() {
            return new PaymentResultBodyProps(this);
        }
    }
}
