package com.mercadopago.android.px.internal.datasource;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.repository.EscManager;
import com.mercadopago.android.px.internal.util.EscUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;

public class EscManagerImp implements EscManager {

    @NonNull private final MercadoPagoESC mercadoPagoESC;

    public EscManagerImp(@NonNull final MercadoPagoESC mercadoPagoESC) {
        this.mercadoPagoESC = mercadoPagoESC;
    }

    @Override
    public boolean hasEsc(@NonNull final Card card) {
        return !TextUtil.isEmpty(mercadoPagoESC.getESC(card.getId()));
    }

    @Override
    public boolean manageEscForPayment(final PaymentData paymentData, final String paymentStatus,
        final String paymentStatusDetail) {
        if (EscUtil.shouldDeleteEsc(paymentData, paymentStatus,
            paymentStatusDetail)) {
            mercadoPagoESC.deleteESC(paymentData.getToken().getCardId());
        } else if (EscUtil.shouldStoreESC(paymentData, paymentStatus, paymentStatusDetail)) {
            mercadoPagoESC.saveESC(paymentData.getToken().getCardId(), paymentData.getToken().getEsc());
        }
        return EscUtil.isInvalidEscPayment(paymentData, paymentStatus, paymentStatusDetail);
    }

    @Override
    public boolean manageEscForError(final MercadoPagoError error, final PaymentData paymentData) {
        final boolean isInvalidEsc = EscUtil.isErrorInvalidPaymentWithEsc(error, paymentData);
        if (isInvalidEsc) {
            mercadoPagoESC.deleteESC(paymentData.getToken().getCardId());
        }
        return isInvalidEsc;
    }
}
