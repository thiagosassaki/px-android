package com.mercadopago.android.px.viewmodel;

import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Payer;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.Token;
import java.io.Serializable;

public final class CheckoutStateModel implements Serializable {

    public Token createdToken;
    public Card selectedCard;
    public Payment createdPayment;
    public Payer collectedPayer;
    public boolean paymentMethodEdited;
    public boolean editPaymentMethodFromReviewAndConfirm;
    public PaymentRecovery paymentRecovery;
    public boolean isUniquePaymentMethod;
    public boolean isOneTap;

    public CheckoutStateModel() {
    }
}
