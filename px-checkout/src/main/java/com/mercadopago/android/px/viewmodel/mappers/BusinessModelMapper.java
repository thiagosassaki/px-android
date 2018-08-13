package com.mercadopago.android.px.viewmodel.mappers;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.core.CheckoutStore;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.plugins.model.BusinessPayment;
import com.mercadopago.android.px.plugins.model.BusinessPaymentModel;

public class BusinessModelMapper extends Mapper<BusinessPayment, BusinessPaymentModel> {

    @NonNull private final PaymentSettingRepository paymentSettingRepository;
    @NonNull private final AmountRepository amountRepository;
    @NonNull private final DiscountRepository discountRepository;
    @NonNull private final PaymentData paymentData;

    public BusinessModelMapper(@NonNull final DiscountRepository discountRepository,
        @NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final AmountRepository amountRepository) {

        this.discountRepository = discountRepository;
        this.paymentSettingRepository = paymentSettingRepository;
        this.amountRepository = amountRepository;
        paymentData = CheckoutStore.getInstance().getPaymentData();
    }

    @Override
    public BusinessPaymentModel map(@NonNull final BusinessPayment val) {

        final String lastFourDigits =
            paymentData.getToken() != null ? paymentData.getToken().getLastFourDigits() : null;

        return new BusinessPaymentModel(val, discountRepository.getDiscount(), paymentData.getPaymentMethod(),
            paymentData.getPayerCost(),
            paymentSettingRepository.getCheckoutPreference().getSite().getCurrencyId(),
            amountRepository.getAmountToPay(), lastFourDigits);
    }
}
