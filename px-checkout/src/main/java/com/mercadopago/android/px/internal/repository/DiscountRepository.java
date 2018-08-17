package com.mercadopago.android.px.internal.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.configuration.PaymentConfiguration;
import com.mercadopago.android.px.internal.base.ResourcesProvider;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import java.math.BigDecimal;

public interface DiscountRepository extends ResourcesProvider {

    void configureMerchantDiscountManually(@Nullable final PaymentConfiguration paymentConfiguration);

    void configureDiscountManually(@Nullable final Discount discount, @Nullable final Campaign campaign);

    @NonNull
    MPCall<Boolean> configureDiscountAutomatically(final BigDecimal amountToPay);

    @NonNull
    MPCall<Discount> getCodeDiscount(@NonNull final BigDecimal amount, @NonNull final String inputCode);

    @Nullable
    Discount getDiscount();

    @Nullable
    String getDiscountCode();

    @Nullable
    Campaign getCampaign();

    @Nullable
    Campaign getCampaign(String discountId);

    boolean isNotAvailableDiscount();

    boolean hasCodeCampaign();

    boolean hasValidDiscount();

    void saveDiscountCode(@NonNull final String code);

    void reset();
}
