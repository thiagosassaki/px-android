package com.mercadopago.android.px.internal.viewmodel;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.Token;

public class CardPaymentModel {

    @NonNull public final Card card;
    @NonNull public Token token;
    @NonNull public PayerCost payerCost;
    @NonNull public Issuer issuer;

    public CardPaymentModel(@NonNull final Card card,
        @NonNull final Token token,
        @NonNull final PayerCost payerCost,
        @NonNull final Issuer issuer) {
        this.card = card;
        this.token = token;
        this.payerCost = payerCost;
        this.issuer = issuer;
    }
}
