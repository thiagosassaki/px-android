package com.mercadopago.android.px.internal.datasource;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;

public class UserSelectionService implements UserSelectionRepository {

    private static final String PREF_SELECTED_PM = "PREF_SELECTED_PAYMENT_METHOD";
    private static final String PREF_SELECTED_PAYER_COST = "PREF_SELECTED_INSTALLMENT";
    private static final String PREF_SELECTED_ISSUER = "PREF_SELECTED_ISSUER";

    @NonNull private final SharedPreferences sharedPreferences;
    @NonNull private final JsonUtil jsonUtil;

    private Card card;

    public UserSelectionService(@NonNull final SharedPreferences sharedPreferences,
        @NonNull final JsonUtil jsonUtil) {
        this.sharedPreferences = sharedPreferences;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void removePaymentMethodSelection() {
        sharedPreferences.edit().remove(PREF_SELECTED_PM).apply();
        removePayerCostSelection();
        removeIssuerSelection();
    }

    private void removeIssuerSelection() {
        sharedPreferences.edit().remove(PREF_SELECTED_ISSUER).apply();
    }

    private void removePayerCostSelection() {
        sharedPreferences.edit().remove(PREF_SELECTED_PAYER_COST).apply();
    }

    private void removeCardSelection() {
        card = null;
        removePaymentMethodSelection();
        removeIssuerSelection();
        removePayerCostSelection();
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
    public boolean hasCardSelected() {
        return getCard() != null;
    }

    /**
     * it's important to select and then add the installments
     * there is a side effect after changing the payment method that
     * deletes the old payer cost cache
     *
     * @param paymentMethod new payment method selected.
     */
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
    public void select(@NonNull final Issuer issuer) {
        sharedPreferences.edit().putString(PREF_SELECTED_ISSUER, jsonUtil.toJson(issuer)).apply();
    }

    @Override
    public void select(@NonNull final Card card) {
        this.card = card;
        select(card.getPaymentMethod());
        select(card.getIssuer());
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

    @Nullable
    @Override
    public Issuer getIssuer() {
        return jsonUtil.fromJson(sharedPreferences.getString(PREF_SELECTED_ISSUER, ""), Issuer.class);
    }

    @Nullable
    @Override
    public Card getCard() {
        return card;
    }

    @Override
    public void reset() {
        removePayerCostSelection();
        removePaymentMethodSelection();
        removeIssuerSelection();
        removeCardSelection();
    }

}
