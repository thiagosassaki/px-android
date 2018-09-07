package com.mercadopago.android.px.internal.datasource;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.TokenRepository;
import com.mercadopago.android.px.internal.services.GatewayService;
import com.mercadopago.android.px.internal.util.EscUtil;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Device;
import com.mercadopago.android.px.model.SavedESCCardToken;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.services.Callback;

public class TokenizeService implements TokenRepository {

    @NonNull private final GatewayService gatewayService;
    @NonNull private final PaymentSettingRepository paymentSettingRepository;
    @NonNull private final MercadoPagoESC mercadoPagoESC;
    @NonNull private final Device device;

    public TokenizeService(@NonNull final GatewayService gatewayService,
        @NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final MercadoPagoESC mercadoPagoESC,
        @NonNull final Device device) {
        this.gatewayService = gatewayService;
        this.paymentSettingRepository = paymentSettingRepository;
        this.mercadoPagoESC = mercadoPagoESC;
        this.device = device;
    }

    @Override
    public MPCall<Token> createToken(@NonNull final Card card) {
        return new MPCall<Token>() {
            @Override
            public void enqueue(final Callback<Token> callback) {
                serviceCallWrapp(card.getId(), mercadoPagoESC.getESC(card.getId())).enqueue(wrap(card, callback));
            }

            @Override
            public void execute(final Callback<Token> callback) {
                serviceCallWrapp(card.getId(), mercadoPagoESC.getESC(card.getId())).enqueue(wrap(card, callback));
            }
        };
    }

    /* default */ Callback<Token> wrap(@NonNull final Card card, final Callback<Token> callback) {
        return new Callback<Token>() {
            @Override
            public void success(final Token token) {
                //TODO move to esc manager  / Token repo
                token.setLastFourDigits(card.getLastFourDigits());
                mercadoPagoESC.saveESC(card.getId(), token.getEsc());
                paymentSettingRepository.configure(token);
                callback.success(token);
            }

            @Override
            public void failure(final ApiException apiException) {
                //TODO move to esc manager  / Token repo
                if (EscUtil.isInvalidEscForApiException(apiException)) {
                    paymentSettingRepository.configure((Token) null);
                    mercadoPagoESC.deleteESC(card.getId());
                }

                callback.failure(apiException);
            }
        };
    }

    /* default */ MPCall<Token> serviceCallWrapp(@NonNull final String cardId, @NonNull final String esc) {
        return gatewayService.getToken(paymentSettingRepository.getPublicKey(),
            paymentSettingRepository.getPrivateKey(),
            SavedESCCardToken.createWithEsc(cardId, esc, device));
    }
}
