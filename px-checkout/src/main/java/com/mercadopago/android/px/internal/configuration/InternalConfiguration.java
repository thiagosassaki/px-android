package com.mercadopago.android.px.internal.configuration;

import java.io.Serializable;

/**
 * Internal configuration provides support for custom checkout functionality/configure special behaviour
 * when checkout is running.
 */
public class InternalConfiguration implements Serializable {

    private final boolean exitOnPaymentMethodChange;

    /* default */ InternalConfiguration(final Builder builder) {
        exitOnPaymentMethodChange = builder.exitOnPaymentMethodChange;
    }

    public boolean shouldExitOnPaymentMethodChange() {
        return exitOnPaymentMethodChange;
    }

    public static class Builder {
        /* default */ boolean exitOnPaymentMethodChange = false;

        /**
         * Enable to do not show payment method selection when
         * payment method is changed
         *
         * @return builder to keep operating
         */
        public Builder exitOnPaymentMethodChange() {
            exitOnPaymentMethodChange = true;
            return this;
        }

        public InternalConfiguration build() {
            return new InternalConfiguration(this);
        }
    }
}
