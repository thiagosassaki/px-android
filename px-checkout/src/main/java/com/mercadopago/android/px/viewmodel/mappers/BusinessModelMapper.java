package com.mercadopago.android.px.viewmodel.mappers;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel;
import com.mercadopago.android.px.internal.viewmodel.mappers.Mapper;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.PaymentData;

public class BusinessModelMapper extends Mapper<BusinessPayment, BusinessPaymentModel> {

    @NonNull private final PaymentSettingRepository paymentSettingRepository;
    @NonNull private final AmountRepository amountRepository;
    @NonNull private final PaymentRepository paymentRepository;
    @NonNull private final DiscountRepository discountRepository;

    public BusinessModelMapper(@NonNull final DiscountRepository discountRepository,
        @NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final AmountRepository amountRepository,
        @NonNull final PaymentRepository paymentRepository) {

        this.discountRepository = discountRepository;
        this.paymentSettingRepository = paymentSettingRepository;
        this.amountRepository = amountRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public BusinessPaymentModel map(@NonNull final BusinessPayment val) {
        final PaymentData paymentData = paymentRepository.getPaymentData();
        final String lastFourDigits =
            paymentData.getToken() != null ? paymentData.getToken().getLastFourDigits() : null;

        return new BusinessPaymentModel(val, discountRepository.getDiscount(), paymentData.getPaymentMethod(),
            paymentData.getPayerCost(),
            paymentSettingRepository.getCheckoutPreference().getSite().getCurrencyId(),
            amountRepository.getAmountToPay(), lastFourDigits);
    }
}
