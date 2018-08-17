package com.mercadopago.android.px.internal.features.providers;

import com.mercadopago.android.px.internal.base.ResourcesProvider;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.SavedCardToken;
import com.mercadopago.android.px.model.SavedESCCardToken;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.exceptions.CardTokenException;

/**
 * Created by marlanti on 7/18/17.
 */

public interface SecurityCodeProvider extends ResourcesProvider {

    String getStandardErrorMessageGotten();

    String getTokenAndCardNotSetMessage();

    String getPaymentMethodNotSetMessage();

    String getCardInfoNotSetMessage();

    String getTokenAndCardWithoutRecoveryCantBeBothSetMessage();

    void cloneToken(final String tokenId, final TaggedCallback<Token> taggedCallback);

    void putSecurityCode(String securityCode, String tokenId, TaggedCallback<Token> taggedCallback);

    void createToken(final SavedCardToken savedCardToken, final TaggedCallback<Token> taggedCallback);

    void createToken(final SavedESCCardToken savedESCCardToken, final TaggedCallback<Token> taggedCallback);

    void validateSecurityCodeFromToken(String mSecurityCode, PaymentMethod mPaymentMethod, String firstSixDigits)
        throws CardTokenException;

    void validateSecurityCodeFromToken(String mSecurityCode) throws CardTokenException;

    void validateSecurityCodeFromToken(SavedCardToken savedCardToken, Card card) throws CardTokenException;

    boolean isESCEnabled();
}
