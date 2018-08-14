package com.mercadopago.android.px.internal.navigation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.preferences.PaymentPreference;
import com.mercadopago.android.px.services.util.TextUtil;

public class DefaultPaymentMethodDriver {

    @NonNull private final PaymentMethodSearch paymentMethods;
    @Nullable private final PaymentPreference preference;

    public DefaultPaymentMethodDriver(@NonNull final PaymentMethodSearch paymentMethods,
        @Nullable final PaymentPreference preference) {
        this.paymentMethods = paymentMethods;
        this.preference = preference;
    }

    public void drive(@NonNull final PaymentMethodDriverCallback paymentMethodDriverCallback) {
        if (preference != null) {
            if (isSavedCard()) {
                final Card card = setUpSavedCard();
                paymentMethodDriverCallback.driveToCardVault(card);
            } else if (isNewCard()) {
                paymentMethodDriverCallback.driveToNewCardFlow();
            } else {
                paymentMethodDriverCallback.doNothing();
            }
        } else {
            paymentMethodDriverCallback.doNothing();
        }
    }

    @NonNull
    private Card setUpSavedCard() {
        final Card card = paymentMethods.getCardById(preference.getDefaultCardId());
        final PaymentMethod paymentMethod =
            paymentMethods.getPaymentMethodById(preference.getDefaultPaymentMethodId());
        if (paymentMethod != null && card.getSecurityCode() == null && paymentMethod.getSettings() != null &&
            paymentMethod.getSettings().get(0) != null) {
            card.setSecurityCode((paymentMethod.getSettings().get(0)).getSecurityCode());
        }
        card.setPaymentMethod(paymentMethod);
        return card;
    }

    private boolean isSavedCard() {
        return isCard() && isValid(preference.getDefaultCardId());
    }

    private boolean isNewCard() {
        return preference.getDefaultCardId() == null &&
            PaymentTypes.isCardPaymentType(preference.getDefaultPaymentTypeId());
    }

    private boolean isValid(@Nullable final String cardId) {
        return !TextUtil.isEmpty(cardId) && paymentMethods.getCardById(cardId) != null;
    }

    private boolean isCard() {
        final PaymentMethod paymentMethod = paymentMethods.getPaymentMethodById(preference.getDefaultPaymentMethodId());
        return paymentMethod != null && PaymentTypes.isCardPaymentType(paymentMethod.getPaymentTypeId());
    }

    public interface PaymentMethodDriverCallback {

        void driveToCardVault(@NonNull final Card card);

        void doNothing();

        void driveToNewCardFlow();
    }
}
