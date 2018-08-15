package com.mercadopago.android.px.internal.datasource;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;

public class UserSelectionService implements UserSelectionRepository {

    private static final String PREF_SELECTED_PM = "PREF_SELECTED_PAYMENT_METHOD";
    private static final String PREF_SELECTED_PAYER_COST = "PREF_SELECTED_INSTALLMENT";

    @NonNull private final SharedPreferences sharedPreferences;
    @NonNull private final JsonUtil jsonUtil;

    public UserSelectionService(@NonNull final SharedPreferences sharedPreferences,
        @NonNull final JsonUtil jsonUtil) {
        this.sharedPreferences = sharedPreferences;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void removePaymentMethodSelection() {
        sharedPreferences.edit().remove(PREF_SELECTED_PM).apply();
        removePayerCostSelection();
    }

    private void removePayerCostSelection() {
        sharedPreferences.edit().remove(PREF_SELECTED_PAYER_COST).apply();
    }

    @Override
    public boolean hasSelectedPaymentMethod() {
        return getPaymentMethod() != null;
    }

    @Override
    public boolean hasPayerCostSelected() {
        return getPayerCost() != null;
    }

    @Override
    public void select(@Nullable final PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            removePaymentMethodSelection();
        } else {
            sharedPreferences.edit().putString(PREF_SELECTED_PM, jsonUtil.toJson(paymentMethod)).apply();
            removePayerCostSelection();
        }
    }

    @Override
    public void select(@NonNull final PayerCost payerCost) {
        sharedPreferences.edit().putString(PREF_SELECTED_PAYER_COST, jsonUtil.toJson(payerCost)).apply();
    }

    @Override
    @Nullable
    public PaymentMethod getPaymentMethod() {
        return jsonUtil.fromJson(sharedPreferences.getString(PREF_SELECTED_PM, ""), PaymentMethod.class);
    }

    @Override
    @Nullable
    public PayerCost getPayerCost() {
        return jsonUtil.fromJson(sharedPreferences.getString(PREF_SELECTED_PAYER_COST, ""), PayerCost.class);
    }

    @Override
    public void reset() {
        removePayerCostSelection();
        removePaymentMethodSelection();
    }
}
