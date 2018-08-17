package com.mercadopago.android.px.internal.features.providers;

import com.mercadopago.android.px.internal.base.ResourcesProvider;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.model.exceptions.CheckoutPreferenceException;

public interface CheckoutProvider extends ResourcesProvider {
    void getCheckoutPreference(String checkoutPreferenceId, TaggedCallback<CheckoutPreference> taggedCallback);

    String getCheckoutExceptionMessage(CheckoutPreferenceException exception);

    String getCheckoutExceptionMessage(IllegalStateException exception);

    void fetchFonts();

    /**
     * Resolve ESC for transaction - delete it if needed
     *
     * @param paymentData the payment information
     * @param paymentStatus the payment status
     * @param paymentStatusDetail the payment detail related with the status
     * @return isInvalidEsc
     */
    boolean manageEscForPayment(final PaymentData paymentData,
        final String paymentStatus,
        final String paymentStatusDetail);

    /**
     * Resolve ESC for transaction - delete it if needed.
     *
     * @param paymentData the payment information
     * @param error the payment error
     * @return isInvalidEsc
     */
    boolean manageEscForError(final MercadoPagoError error, final PaymentData paymentData);
}
