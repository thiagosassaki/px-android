package com.mercadopago.android.px.internal.viewmodel;

import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.IPayment;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.Token;
import java.io.Serializable;

public final class CheckoutStateModel implements Serializable {

    public Token createdToken;
    public Card selectedCard;
    public boolean paymentMethodEdited;
    public boolean editPaymentMethodFromReviewAndConfirm;
    public PaymentRecovery paymentRecovery;
    public boolean isUniquePaymentMethod;
    public boolean isOneTap;
    public IPayment createdPayment;

    public CheckoutStateModel() {
    }
}
