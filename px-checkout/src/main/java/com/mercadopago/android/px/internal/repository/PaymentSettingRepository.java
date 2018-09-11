package com.mercadopago.android.px.internal.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.configuration.AdvancedConfiguration;
import com.mercadopago.android.px.configuration.PaymentConfiguration;
import com.mercadopago.android.px.internal.configuration.InternalConfiguration;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.commission.ChargeRule;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import java.util.List;

public interface PaymentSettingRepository {

    void reset();

    void configure(@NonNull final AdvancedConfiguration advancedConfiguration);

    void configure(@NonNull String publicKey);

    void configure(@Nullable CheckoutPreference checkoutPreference);

    void configure(@Nullable final PaymentConfiguration paymentConfiguration);

    void configure(@NonNull Token token);

    void configurePreferenceId(@Nullable String preferenceId);

    void configurePrivateKey(@Nullable final String privateKey);

    @NonNull
    List<ChargeRule> chargeRules();

    @NonNull
    PaymentConfiguration getPaymentConfiguration();

    @Nullable
    CheckoutPreference getCheckoutPreference();

    @Nullable
    String getCheckoutPreferenceId();

    @NonNull
    String getPublicKey();

    @NonNull
    String getTransactionId();

    @NonNull
    AdvancedConfiguration getAdvancedConfiguration();

    @Nullable
    String getPrivateKey();

    @Nullable
    Token getToken();
}
