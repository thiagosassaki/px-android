package com.mercadopago.android.px.plugins;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.plugins.model.PaymentMethodInfo;
import com.mercadopago.example.R;

public class SamplePaymentMethodPlugin extends PaymentMethodPlugin {

    public SamplePaymentMethodPlugin() {
        super();
    }

    @Override
    public void init() {
        //Do nothing
    }

    @Override
    @NonNull
    public PaymentMethodInfo getPaymentMethodInfo(@NonNull final Context context) {
        return new PaymentMethodInfo(
            getId(),
            "Dinero en cuenta",
            R.drawable.px_sample,
            "Custom payment method"
        );
    }

    @Nullable
    @Override
    public Bundle getFragmentBundle(@NonNull final CheckoutData data,
        @NonNull final Context context) {
        return new Bundle();
    }

    @Override
    public boolean shouldShowFragmentOnSelection() {
        return true;
    }

    @Nullable
    @Override
    public PluginFragment getFragment(@NonNull final CheckoutData data,
        @NonNull final Context context) {
        return new SamplePluginFragment();
    }

    @Override
    public PluginPosition getPluginPosition() {
        return PluginPosition.BOTTOM;
    }

    @NonNull
    @Override
    public String getId() {
        return "account_money";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
