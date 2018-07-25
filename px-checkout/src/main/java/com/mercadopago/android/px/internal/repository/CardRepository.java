package com.mercadopago.android.px.internal.repository;

import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Token;

public interface CardRepository {
    Card getCard();

    Token getToken();
}
