package com.mercadopago.android.px.internal.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.model.commission.ChargeRule;
import com.mercadopago.android.px.preferences.AdvancedConfiguration;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import java.util.List;

public interface PaymentSettingRepository {

    void reset();

    void configure(@NonNull List<ChargeRule> charges);

    void configure(@NonNull final AdvancedConfiguration advancedConfiguration);

    void configure(@NonNull String publicKey);

    void configure(@Nullable CheckoutPreference checkoutPreference);

    void configurePreferenceId(@Nullable String preferenceId);

    @NonNull
    List<ChargeRule> chargeRules();

    @Nullable
    CheckoutPreference getCheckoutPreference();

    @Nullable
    String getCheckoutPreferenceId();

    @NonNull
    String getPublicKey();

    @NonNull
    AdvancedConfiguration getAdvancedConfiguration();

    @Nullable
    String getPrivateKey();

    void configurePrivateKey(@Nullable final String privateKey);
}
