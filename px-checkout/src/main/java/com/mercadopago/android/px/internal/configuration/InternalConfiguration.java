package com.mercadopago.android.px.internal.configuration;

/**
 * Internal configuration provides support to money in flow for custom checkout functionality/configure special behaviour
 * when checkout is running.
 * {@see <a href="https://github.com/mercadolibre/fury_moneyin-android">Money In repository</a>}
 */
@SuppressWarnings("unused")
public class InternalConfiguration {

    private final boolean exitOnPaymentMethodChange;

    /**
     * Enable to do not show payment method selection when
     * payment method is changed.
     */
    public InternalConfiguration(final boolean exitOnPaymentMethodChange) {
        this.exitOnPaymentMethodChange = exitOnPaymentMethodChange;
    }

    public boolean shouldExitOnPaymentMethodChange() {
        return exitOnPaymentMethodChange;
    }
}
