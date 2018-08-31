package com.mercadopago.android.px.internal.repository;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;

public interface EscManager {

    /**
     * Verify if exists ESC saved for certain card.
     *
     * @param card your card
     * @return true if has ESC, false otherwise.
     */
    boolean hasEsc(@NonNull final Card card);

    /**
     * Resolve ESC for transaction - delete it if needed
     *
     * @param paymentData the payment information
     * @param paymentStatus the payment status
     * @param paymentStatusDetail the payment detail related with the status
     * @return isInvalidEsc
     */
    boolean manageEscForPayment(final PaymentData paymentData,
        final String paymentStatus,
        final String paymentStatusDetail);

    /**
     * Resolve ESC for transaction - delete it if needed.
     *
     * @param paymentData the payment information
     * @param error the payment error
     * @return isInvalidEsc
     */
    boolean manageEscForError(final MercadoPagoError error, final PaymentData paymentData);
}
