package com.mercadopago.android.px.preferences;

import java.io.Serializable;

/**
 * Advanced configuration allows you to configure special behaviour
 * when checkout is running.
 */
public class AdvancedConfiguration implements Serializable {

    private final boolean bankDealsEnabled;
    private final boolean escEnabled;
    private final boolean isBinaryMode;

    /* default */ AdvancedConfiguration(final Builder builder) {
        bankDealsEnabled = builder.bankDealsEnabled;
        escEnabled = builder.escEnabled;
        isBinaryMode = builder.isBinaryMode;
    }

    public boolean isBankDealsEnabled() {
        return bankDealsEnabled;
    }

    public boolean isEscEnabled() {
        return escEnabled;
    }

    public boolean isBinaryMode() {
        return isBinaryMode;
    }

    @SuppressWarnings("unused")
    public static class Builder {
        /* default */ boolean bankDealsEnabled = true;
        /* default */ boolean escEnabled = false;
        /* default */ boolean isBinaryMode = false;

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

        /**
         * If enableBinaryMode is called, processed payment can only be APPROVED or REJECTED.
         * Default value is false.
         * <p>
         * Non compatible with PaymentProcessor.
         * <p>
         * Non compatible with off payments methods
         *
         * @return builder
         */
        public Builder setBinaryMode(final boolean isBinaryMode) {
            this.isBinaryMode = isBinaryMode;
            return this;
        }

        public AdvancedConfiguration build() {
            return new AdvancedConfiguration(this);
        }
    }
}
