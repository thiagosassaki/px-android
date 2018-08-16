package com.mercadopago.android.px.internal.features.plugins;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.internal.util.TextUtil;

public class SamplePaymentMethodPresenter {

    private static final String PASSWORD = "123";
    private static final int DELAY_MILLIS = 2000;
    /* default */ @Nullable SamplePaymentMethodPluginFragment samplePaymentMethodPluginFragment;
    /* default */ final SampleResources resources;
    /* default */ SampleState state;

    public SamplePaymentMethodPresenter(
        @NonNull final SampleResources resources) {
        this.resources = resources;
        state = new SamplePaymentMethodPresenter.SampleState(false, "", "");

    }

    public void authenticate(final String password) {

        if (TextUtil.isEmpty(password)) {
            state = new SampleState(false,
                resources.getPasswordRequiredMessage(),
                "");
            update();
        } else {

            state = new SampleState(true, null, password);
            update();
            // Simular llamada API....
            // En otro thread
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        Thread.sleep(DELAY_MILLIS);
                    } catch (final InterruptedException e) {
                        //nada
                    }

                    if (PASSWORD.equals(password)) {
                        if (samplePaymentMethodPluginFragment != null) {
                            samplePaymentMethodPluginFragment.next();
                        }
                    } else {
                        state = new SampleState(false, resources.getPasswordErrorMessage(), password);
                        update();
                    }
                }
            }).start();
        }
    }

    /* default */ void update() {
        if (samplePaymentMethodPluginFragment != null) {
            samplePaymentMethodPluginFragment.update(state);
        }
    }

    public void init(final SamplePaymentMethodPluginFragment samplePaymentMethodPluginFragment) {
        this.samplePaymentMethodPluginFragment = samplePaymentMethodPluginFragment;
        this.samplePaymentMethodPluginFragment.update(state);
    }

    public static class SampleState {

        public final boolean authenticating;
        public final String password;
        public final String errorMessage;

        public SampleState(final boolean authenticating, final String errorMessage, final String password) {
            this.authenticating = authenticating;
            this.errorMessage = errorMessage;
            this.password = password;
        }
    }
}