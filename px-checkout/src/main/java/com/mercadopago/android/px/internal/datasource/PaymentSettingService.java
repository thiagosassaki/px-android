package com.mercadopago.android.px.internal.datasource;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.commission.ChargeRule;
import com.mercadopago.android.px.model.commission.PaymentMethodRule;
import com.mercadopago.android.px.preferences.AdvancedConfiguration;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.util.JsonUtil;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PaymentSettingService implements PaymentSettingRepository {

    private static final String PREF_CHARGES = "PREF_CHARGES";
    private static final String PREF_CHECKOUT_PREF = "PREF_CHECKOUT_PREFERENCE";
    private static final String PREF_CHECKOUT_PREF_ID = "PREF_CHECKOUT_PREFERENCE_ID";
    private static final String PREF_PUBLIC_KEY = "PREF_PUBLIC_KEY";
    private static final String PREF_PRIVATE_KEY = "PREF_PRIVATE_KEY";
    private static final String PREF_BINARY_MODE = "PREF_BINARY_MODE";
    private static final String PREF_TOKEN = "PREF_TOKEN";
    private static final String PREF_ADVANCED = "PREF_ADVANCED";

    @NonNull private final SharedPreferences sharedPreferences;
    @NonNull private final JsonUtil jsonUtil;

    //mem cache
    private CheckoutPreference pref;

    public PaymentSettingService(@NonNull final SharedPreferences sharedPreferences, @NonNull final JsonUtil jsonUtil) {
        this.sharedPreferences = sharedPreferences;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void reset() {
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.clear().apply();
        pref = null;
    }

    @Override
    public void configurePreferenceId(@Nullable final String preferenceId) {
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(PREF_CHECKOUT_PREF_ID, preferenceId).apply();
    }

    @Override
    public void configure(final boolean binaryMode) {
        sharedPreferences.edit().putBoolean(PREF_BINARY_MODE, binaryMode).apply();
    }

    @Override
    public void configure(@NonNull final Token token) {
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(PREF_TOKEN, jsonUtil.toJson(token));
        edit.apply();
    }

    @Override
    public void configure(@NonNull final List<ChargeRule> charges) {
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(PREF_CHARGES, jsonUtil.toJson(charges));
        edit.apply();
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
    public boolean isBinaryMode() {
        return sharedPreferences.getBoolean(PREF_BINARY_MODE, false);
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
        final Type listType = new TypeToken<List<PaymentMethodRule>>() {
        }.getType();
        return jsonUtil.fromJson(sharedPreferences.getString(PREF_CHARGES, ""), listType);
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

    @Nullable
    @Override
    public Token getToken() {
        return jsonUtil.fromJson(sharedPreferences.getString(PREF_TOKEN, ""), Token.class);
    }

    @NonNull
    @Override
    public String getTransactionId() {
        return String.format(Locale.getDefault(), "%s%d", getPublicKey(), Calendar.getInstance().getTimeInMillis());
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
