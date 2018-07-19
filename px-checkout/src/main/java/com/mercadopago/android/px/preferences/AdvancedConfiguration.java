package com.mercadopago.android.px.preferences;

import java.io.Serializable;

/**
 * Advanced configuration allows you to configure special behaviour
 * when checkout is running.
 */
public final class AdvancedConfiguration implements Serializable {

    private final boolean bankDealsEnabled;
    private final boolean escEnabled;

    /* default */ AdvancedConfiguration(final Builder builder) {
        bankDealsEnabled = builder.bankDealsEnabled;
        escEnabled = builder.escEnabled;
    }

    public boolean isBankDealsEnabled() {
        return bankDealsEnabled;
    }

    public boolean isEscEnabled() {
        return escEnabled;
    }

    @SuppressWarnings("unused")
    public static class Builder {
        /* default */ boolean bankDealsEnabled = true;
        /* default */ boolean escEnabled = false;

        /**
         * Add the possibility to configure Bank's deals behaviour.
         * If set as true, then the checkout will try to retrieve bank deals.
         * If set as false, then the checkout will not try to retrieve bank deals.
         *
         * @param bankDealsEnabled bool that reflects it's behaviour
         * @return builder
         */
        public Builder setBankDealsEnabled(final boolean bankDealsEnabled) {
            this.bankDealsEnabled = bankDealsEnabled;
            return this;
        }

        /**
         * Add the possibility to configure ESC behaviour.
         * If set as true, then saved cards will try to use ESC feature.
         * If set as false, then security code will be always asked.
         *
         * @param escEnabled bool that reflects it's behaviour
         * @return builder
         */
        public Builder setEscEnabled(final boolean escEnabled) {
            this.escEnabled = escEnabled;
            return this;
        }

        public AdvancedConfiguration build() {
            return new AdvancedConfiguration(this);
        }
    }
}
