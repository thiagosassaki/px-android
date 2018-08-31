package com.mercadopago.android.px.internal.callbacks;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.repository.EscManager;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.GenericPayment;
import com.mercadopago.android.px.model.IPayment;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;

public final class PaymentServiceHandlerWrapper implements PaymentServiceHandler {

    @NonNull private final PaymentServiceHandler handler;
    @NonNull private final PaymentRepository paymentRepository;
    @NonNull private final EscManager escManager;

    public PaymentServiceHandlerWrapper(
        @NonNull final PaymentServiceHandler handler,
        @NonNull final PaymentRepository paymentRepository,
        @NonNull final EscManager escManager) {
        this.handler = handler;
        this.paymentRepository = paymentRepository;
        this.escManager = escManager;
    }

    @Override
    public void onCvvRequired(@NonNull final Card card) {
        handler.onCvvRequired(card);
    }

    @Override
    public void onVisualPayment() {
        handler.onVisualPayment();
    }

    @Override
    public void onRecoverPaymentEscInvalid() {
        handler.onRecoverPaymentEscInvalid();
    }

    @Override
    public void onPaymentFinished(@NonNull final Payment payment) {
        if (handleEsc(payment)) {
            onRecoverPaymentEscInvalid();
        } else {
            handler.onPaymentFinished(payment);
        }
    }

    @Override
    public void onPaymentFinished(@NonNull final GenericPayment genericPayment) {
        if (handleEsc(genericPayment)) {
            onRecoverPaymentEscInvalid();
        } else {
            handler.onPaymentFinished(genericPayment);
        }
    }

    @Override
    public void onPaymentFinished(@NonNull final BusinessPayment businessPayment) {
        handleEsc(businessPayment);
        handler.onPaymentFinished(businessPayment);
    }

    @Override
    public void onPaymentError(@NonNull final MercadoPagoError error) {
        if (handleEsc(error)) {
            onRecoverPaymentEscInvalid();
        } else {
            handler.onPaymentError(error);
        }
    }

    private boolean handleEsc(final MercadoPagoError error) {
        return escManager.manageEscForError(error, paymentRepository.getPaymentData());
    }

    private boolean handleEsc(final IPayment payment) {
        return escManager.manageEscForPayment(paymentRepository.getPaymentData(),
            payment.getPaymentStatus(),
            payment.getPaymentStatusDetail());
    }
}
