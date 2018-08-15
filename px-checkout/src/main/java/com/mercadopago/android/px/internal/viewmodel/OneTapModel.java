package com.mercadopago.android.px.internal.viewmodel;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import java.io.Serializable;

/**
 * All this information can be obtained by {@link com.mercadopago.android.px.internal.di.Session}
 */
@Deprecated
public class OneTapModel implements Serializable {

    //TODO remove all.
    private final PaymentMethodSearch paymentMethods;
    @NonNull private final String publicKey;

    private OneTapModel(@NonNull final PaymentMethodSearch paymentMethods,
        @NonNull final String publicKey) {

        this.paymentMethods = paymentMethods;
        this.publicKey = publicKey;
    }

    public static OneTapModel from(final PaymentMethodSearch groups,
        final PaymentSettingRepository paymentSettingRepository) {
        return new OneTapModel(groups,
            paymentSettingRepository.getPublicKey());
    }

    public PaymentMethodSearch getPaymentMethods() {
        return paymentMethods;
    }

    @NonNull
    public String getPublicKey() {
        return publicKey;
    }
}
