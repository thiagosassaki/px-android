package com.mercadopago.android.px.plugins;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public abstract class PaymentMethodPluginFragment extends Fragment {

    public interface onPaymentMethodActions {
        void next();

        void back();
    }

    @Nullable
    private onPaymentMethodActions onPaymentMethodActions;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof onPaymentMethodActions) {
            onPaymentMethodActions = (onPaymentMethodActions) context;
        }
    }

    @Override
    public void onDetach() {
        onPaymentMethodActions = null;
        super.onDetach();
    }

    @Nullable
    protected onPaymentMethodActions getPaymentMethodActions() {
        return onPaymentMethodActions;
    }
}
