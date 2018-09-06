package com.mercadopago.android.px.internal.viewmodel;

import com.mercadopago.android.px.model.IPayment;
import com.mercadopago.android.px.model.PaymentRecovery;
import java.io.Serializable;

public final class CheckoutStateModel implements Serializable {

    public boolean paymentMethodEdited;
    public boolean editPaymentMethodFromReviewAndConfirm;
    public PaymentRecovery paymentRecovery;
    public boolean isUniquePaymentMethod;
    public boolean isOneTap;
    public IPayment createdPayment;

    public CheckoutStateModel() {
    }
}
