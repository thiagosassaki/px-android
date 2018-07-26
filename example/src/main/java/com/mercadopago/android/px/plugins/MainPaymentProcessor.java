package com.mercadopago.android.px.plugins;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.plugins.model.PluginPayment;

public class MainPaymentProcessor extends PaymentProcessor {

    /* default */ MainPaymentProcessor(final Parcel in) {
        //Do nothing
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        //Do nothing
    }

    public static final Creator<MainPaymentProcessor> CREATOR = new Creator<MainPaymentProcessor>() {
        @Override
        public MainPaymentProcessor createFromParcel(final Parcel in) {
            return new MainPaymentProcessor(in);
        }

        @Override
        public MainPaymentProcessor[] newArray(final int size) {
            return new MainPaymentProcessor[size];
        }
    };

    private PluginPayment pluginPayment;

    public MainPaymentProcessor(final PluginPayment pluginPayment) {
        this.pluginPayment = pluginPayment;
    }

    @Override
    public void initPayment(@NonNull final Context appContext, @NonNull final Props props,
        @NonNull final OnPaymentListener paymentListener) {

    }
}