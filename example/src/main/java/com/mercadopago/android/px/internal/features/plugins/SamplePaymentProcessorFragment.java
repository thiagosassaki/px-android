package com.mercadopago.android.px.internal.features.plugins;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.core.PaymentProcessor;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.GenericPayment;
import com.mercadopago.android.px.model.IPayment;
import com.mercadopago.example.R;

public class SamplePaymentProcessorFragment extends Fragment {

    private static final long CONST_TIME_MILLIS = 2000;
    @Nullable
    private IPayment payment;
    @Nullable
    private PaymentProcessor.OnPaymentListener paymentListener;

    public void setPayment(@Nullable final IPayment payment) {
        this.payment = payment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.px_view_progress_bar, container, false);
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof PaymentProcessor.OnPaymentListener) {
            paymentListener = (PaymentProcessor.OnPaymentListener) context;
        }
    }

    @Override
    public void onDetach() {
        paymentListener = null;
        super.onDetach();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (paymentListener != null && payment != null) {
                    if (payment instanceof BusinessPayment) {
                        paymentListener.onPaymentFinished((BusinessPayment) payment);
                    } else if (payment instanceof GenericPayment) {
                        paymentListener.onPaymentFinished((GenericPayment) payment);
                    }
                }
            }
        }, CONST_TIME_MILLIS);
    }
}
