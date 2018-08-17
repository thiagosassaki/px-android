package com.mercadopago.android.px.internal.viewmodel.mappers;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.viewmodel.OneTapModel;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.CardPaymentMetadata;
import com.mercadopago.android.px.model.OneTapMetadata;
import com.mercadopago.android.px.model.PaymentMethod;

public class CardMapper extends Mapper<OneTapModel, Card> {

    @Override
    public Card map(@NonNull final OneTapModel val) {
        final OneTapMetadata oneTapMetadata = val.getPaymentMethods().getOneTapMetadata();
        final CardPaymentMetadata cardPaymentMetadata = oneTapMetadata.getCard();
        final PaymentMethod paymentMethod =
            val.getPaymentMethods().getPaymentMethodById(oneTapMetadata.getPaymentMethodId());
        final Card card = val.getPaymentMethods().getCardById(cardPaymentMetadata.getId());
        card.setSecurityCode(paymentMethod != null ? paymentMethod.getSecurityCode() : null);
        card.setPaymentMethod(paymentMethod);
        return card;
    }
}
