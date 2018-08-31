package com.mercadopago.android.px.internal.repository;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Token;

public interface TokenRepository {

    MPCall<Token> createToken(@NonNull final Card card);
}
