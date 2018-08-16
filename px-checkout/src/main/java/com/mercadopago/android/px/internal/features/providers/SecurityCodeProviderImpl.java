package com.mercadopago.android.px.internal.features.providers;

import android.content.Context;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.datasource.MercadoPagoServicesAdapter;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.internal.datasource.MercadoPagoESC;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.CardToken;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.SavedCardToken;
import com.mercadopago.android.px.model.SavedESCCardToken;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.requests.SecurityCodeIntent;
import com.mercadopago.android.px.model.exceptions.CardTokenException;

public class SecurityCodeProviderImpl implements SecurityCodeProvider {

    private final Context mContext;
    private final MercadoPagoServicesAdapter mMercadoPagoServicesAdapter;
    private final MercadoPagoESC mercadoPagoESC;

    private static final String TOKEN_AND_CARD_NOT_SET_MESSAGE = "token and card can't both be null";
    private static final String TOKEN_AND_CARD_WITHOUT_RECOVERY_SET_MESSAGE =
        "can't set token and card at the same time without payment recovery";
    private static final String PAYMENT_METHOD_NOT_SET = "payment method not set";
    private static final String CARD_INFO_NOT_SET = "card info can't be null";

    public SecurityCodeProviderImpl(@NonNull final Context context) {
        mContext = context;
        final Session session = Session.getSession(context);
        mMercadoPagoServicesAdapter = session.getMercadoPagoServiceAdapter();
        mercadoPagoESC = session.getMercadoPagoESC();
    }

    @Override
    public boolean isESCEnabled() {
        return mercadoPagoESC.isESCEnabled();
    }

    @Override
    public String getStandardErrorMessageGotten() {
        return mContext.getString(R.string.px_standard_error_message);
    }

    @Override
    public String getCardInfoNotSetMessage() {
        return CARD_INFO_NOT_SET;
    }

    @Override
    public String getPaymentMethodNotSetMessage() {
        return PAYMENT_METHOD_NOT_SET;
    }

    @Override
    public String getTokenAndCardWithoutRecoveryCantBeBothSetMessage() {
        return TOKEN_AND_CARD_WITHOUT_RECOVERY_SET_MESSAGE;
    }

    @Override
    public String getTokenAndCardNotSetMessage() {
        return TOKEN_AND_CARD_NOT_SET_MESSAGE;
    }

    @Override
    public void cloneToken(final String tokenId, final TaggedCallback<Token> taggedCallback) {
        mMercadoPagoServicesAdapter.cloneToken(tokenId, taggedCallback);
    }

    @Override
    public void putSecurityCode(final String securityCode, final String tokenId,
        final TaggedCallback<Token> taggedCallback) {
        SecurityCodeIntent securityCodeIntent = new SecurityCodeIntent();
        securityCodeIntent.setSecurityCode(securityCode);
        mMercadoPagoServicesAdapter.putSecurityCode(tokenId, securityCodeIntent, taggedCallback);
    }

    @Override
    public void createToken(final SavedCardToken savedCardToken, final TaggedCallback<Token> taggedCallback) {
        mMercadoPagoServicesAdapter.createToken(savedCardToken, taggedCallback);
    }

    @Override
    public void createToken(SavedESCCardToken savedESCCardToken, final TaggedCallback<Token> taggedCallback) {
        mMercadoPagoServicesAdapter.createToken(savedESCCardToken, taggedCallback);
    }

    @Override
    public void validateSecurityCodeFromToken(String securityCode, PaymentMethod paymentMethod, String firstSixDigits)
        throws CardTokenException {
        CardToken.validateSecurityCode(securityCode, paymentMethod, firstSixDigits);
    }

    @Override
    public void validateSecurityCodeFromToken(String securityCode) throws CardTokenException {
        if (!CardToken.validateSecurityCode(securityCode)) {
            throw new CardTokenException(CardTokenException.INVALID_FIELD);
        }
    }

    @Override
    public void validateSecurityCodeFromToken(SavedCardToken savedCardToken, Card card) throws CardTokenException {
        savedCardToken.validateSecurityCode(card);
    }
}
