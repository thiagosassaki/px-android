package com.mercadopago.android.px.plugins;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class PaymentProcessorFragment extends Fragment {

    @Nullable
    private PaymentProcessor.OnPaymentListener onPaymentListener;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof PaymentProcessor.OnPaymentListener) {
            onPaymentListener = (PaymentProcessor.OnPaymentListener) context;
        }
    }

    @Override
    public void onDetach() {
        onPaymentListener = null;
        super.onDetach();
    }

    @Nullable
    protected PaymentProcessor.OnPaymentListener getOnPaymentListener() {
        return onPaymentListener;
    }
}
