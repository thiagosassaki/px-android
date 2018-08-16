package com.mercadopago.android.px.internal.datasource;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.internal.services.DiscountService;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import java.math.BigDecimal;
import java.util.List;
import retrofit2.Retrofit;

public class DiscountApiService {

    @NonNull private final DiscountService discountService;
    @NonNull private final PaymentSettingRepository paymentSettingRepository;

    public DiscountApiService(@NonNull final Retrofit retrofit,
        @NonNull final PaymentSettingRepository paymentSettingRepository) {
        discountService = retrofit.create(DiscountService.class);
        this.paymentSettingRepository = paymentSettingRepository;
    }

    @NonNull
    public MPCall<List<Campaign>> getCampaigns() {
        return discountService.getCampaigns(paymentSettingRepository.getPublicKey(), getEmail());
    }

    @NonNull
    public MPCall<Discount> getDiscount(@NonNull final BigDecimal amount) {
        return discountService.getDiscount(paymentSettingRepository.getPublicKey(), amount.toString(), getEmail());
    }

    @NonNull
    public MPCall<Discount> getCodeDiscount(@NonNull final BigDecimal amount, @NonNull final String inputCode) {
        return discountService
            .getDiscount(paymentSettingRepository.getPublicKey(), amount.toString(), getEmail(), inputCode);
    }

    @NonNull
    private String getEmail() {
        return paymentSettingRepository.getCheckoutPreference().getPayer().getEmail();
    }
}
