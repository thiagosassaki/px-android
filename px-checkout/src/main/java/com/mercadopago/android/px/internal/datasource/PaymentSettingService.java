package com.mercadopago.android.px.internal.datasource;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.model.commission.ChargeRule;
import com.mercadopago.android.px.preferences.AdvancedConfiguration;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.preferences.PaymentConfiguration;
import com.mercadopago.android.px.util.JsonUtil;
import java.util.ArrayList;
import java.util.List;

public class PaymentSettingService implements PaymentSettingRepository {

    private static final String PREF_CHECKOUT_PREF = "PREF_CHECKOUT_PREFERENCE";
    private static final String PREF_CHECKOUT_PREF_ID = "PREF_CHECKOUT_PREFERENCE_ID";
    private static final String PREF_PUBLIC_KEY = "PREF_PUBLIC_KEY";
    private static final String PREF_PRIVATE_KEY = "PREF_PRIVATE_KEY";
    private static final String PREF_ADVANCED = "PREF_ADVANCED";

    @NonNull private final SharedPreferences sharedPreferences;
    @NonNull private final JsonUtil jsonUtil;

    //mem cache
    private CheckoutPreference pref;
    //TODO add persistance.
    private PaymentConfiguration paymentConfiguration;

    public PaymentSettingService(@NonNull final SharedPreferences sharedPreferences, @NonNull final JsonUtil jsonUtil) {
        this.sharedPreferences = sharedPreferences;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void reset() {
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.clear().apply();
        pref = null;
        paymentConfiguration = null;
    }

    @Override
    public void configurePreferenceId(@Nullable final String preferenceId) {
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(PREF_CHECKOUT_PREF_ID, preferenceId).apply();
    }

    @Override
    public void configure(@NonNull final AdvancedConfiguration advancedConfiguration) {
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(PREF_ADVANCED, jsonUtil.toJson(advancedConfiguration));
        edit.apply();
    }

    @Override
    public void configure(@NonNull final String publicKey) {
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(PREF_PUBLIC_KEY, publicKey);
        edit.apply();
    }

    @Override
    public void configurePrivateKey(@Nullable final String privateKey) {
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(PREF_PRIVATE_KEY, privateKey);
        edit.apply();
    }

    @Override
    public void configure(@Nullable final PaymentConfiguration paymentConfiguration) {
        this.paymentConfiguration = paymentConfiguration;
    }

    @Override
    public boolean hasPaymentConfiguration() {
        return getPaymentConfiguration() != null;
    }

    @Override
    public void configure(@Nullable final CheckoutPreference checkoutPreference) {
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        if (checkoutPreference == null) {
            edit.remove(PREF_CHECKOUT_PREF).apply();
        } else {
            edit.putString(PREF_CHECKOUT_PREF, jsonUtil.toJson(checkoutPreference));
            edit.apply();
        }
        pref = checkoutPreference;
    }

    @NonNull
    @Override
    public List<ChargeRule> chargeRules() {
        final PaymentConfiguration paymentConfiguration = getPaymentConfiguration();
        return paymentConfiguration == null ? new ArrayList<ChargeRule>() :
            paymentConfiguration.getCharges();
    }

    @Nullable
    @Override
    public PaymentConfiguration getPaymentConfiguration() {
        return paymentConfiguration;
    }

    @Nullable
    @Override
    public CheckoutPreference getCheckoutPreference() {
        if (pref == null) {
            pref = jsonUtil.fromJson(sharedPreferences.getString(PREF_CHECKOUT_PREF, ""), CheckoutPreference.class);
        }
        return pref;
    }

    @Nullable
    @Override
    public String getCheckoutPreferenceId() {
        return sharedPreferences.getString(PREF_CHECKOUT_PREF_ID, null);
    }

    @NonNull
    @Override
    public String getPublicKey() {
        return sharedPreferences.getString(PREF_PUBLIC_KEY, "");
    }

    @NonNull
    @Override
    public AdvancedConfiguration getAdvancedConfiguration() {
        // should never be null - see MercadoPagoCheckout
        return jsonUtil.fromJson(sharedPreferences.getString(PREF_ADVANCED, ""), AdvancedConfiguration.class);
    }

    @Nullable
    @Override
    public String getPrivateKey() {
        return sharedPreferences.getString(PREF_PRIVATE_KEY, null);
    }
}
