package com.mercadopago.android.px.internal.configuration;

import java.io.Serializable;

/**
 * Internal configuration provides support to money in for custom checkout functionality/configure special behaviour
 * when checkout is running.
 *
 * @see <a href="https://github.com/mercadolibre/fury_moneyin-android">https://github.com/mercadolibre/fury_moneyin-android</a>
 */
@SuppressWarnings("unused")
public class InternalConfiguration implements Serializable {

    private final boolean exitOnPaymentMethodChange;

    /**
     * Enable to do not show payment method selection when
     * payment method is changed
     */
    public InternalConfiguration(final boolean exitOnPaymentMethodChange) {
        this.exitOnPaymentMethodChange = exitOnPaymentMethodChange;
    }

    public boolean shouldExitOnPaymentMethodChange() {
        return exitOnPaymentMethodChange;
    }
}
