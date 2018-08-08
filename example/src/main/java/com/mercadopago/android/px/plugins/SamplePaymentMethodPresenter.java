package com.mercadopago.android.px.plugins;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.util.TextUtils;

public class SamplePaymentMethodPresenter {

    private static final String PASSWORD = "123";
    /* default */ @Nullable SamplePluginFragment samplePluginFragment;
    /* default */ final SampleResources resources;
    /* default */ SampleState state;

    public SamplePaymentMethodPresenter(
        @NonNull final SampleResources resources) {
        this.resources = resources;
        state = new SamplePaymentMethodPresenter.SampleState(false, "", "");

    }

    public void authenticate(final String password) {

        if (TextUtils.isEmpty(password)) {
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
                        Thread.sleep(2000);
                    } catch (final InterruptedException e) {
                        //nada
                    }

                    if (PASSWORD.equals(password)) {
                        if (samplePluginFragment != null) {
                            samplePluginFragment.getPaymentMethodActions().next();
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
        if (samplePluginFragment != null) {
            samplePluginFragment.update(state);
        }
    }

    public void init(final SamplePluginFragment samplePluginFragment) {
        this.samplePluginFragment = samplePluginFragment;
        this.samplePluginFragment.update(state);
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