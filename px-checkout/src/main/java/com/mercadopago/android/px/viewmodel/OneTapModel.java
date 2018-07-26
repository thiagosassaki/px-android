package com.mercadopago.android.px.viewmodel;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.review_and_confirm.models.ReviewAndConfirmPreferences;
import java.io.Serializable;

public class OneTapModel implements Serializable {

    private final PaymentMethodSearch paymentMethods;
    private final boolean isEscEnabled;
    @DrawableRes
    @Nullable
    private final Integer collectorIcon;
    @NonNull private final String publicKey;

    private OneTapModel(@NonNull final PaymentMethodSearch paymentMethods,
        final boolean isEscEnabled,
        @NonNull final String publicKey,
        @Nullable final Integer collectorIcon) {

        this.paymentMethods = paymentMethods;
        this.isEscEnabled = isEscEnabled;
        this.collectorIcon = collectorIcon;
        this.publicKey = publicKey;
    }

    public static OneTapModel from(final PaymentMethodSearch groups,
        final PaymentSettingRepository paymentSettingRepository,
        final ReviewAndConfirmPreferences reviewAndConfirmPreferences) {
        return new OneTapModel(groups,
            paymentSettingRepository.getAdvancedConfiguration().isEscEnabled(),
            paymentSettingRepository.getPublicKey(),
            reviewAndConfirmPreferences.getCollectorIcon());
    }

    public PaymentMethodSearch getPaymentMethods() {
        return paymentMethods;
    }

    public boolean isEscEnabled() {
        return isEscEnabled;
    }

    @Nullable
    public Integer getCollectorIcon() {
        return collectorIcon;
    }

    @NonNull
    public String getPublicKey() {
        return publicKey;
    }
}
