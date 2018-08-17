package com.mercadopago.android.px.viewmodel.mappers;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.viewmodel.mappers.Mapper;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.Payer;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.requests.PaymentBodyIntent;

import static com.mercadopago.android.px.internal.util.TextUtil.isEmpty;

public class PaymentDataMapper extends Mapper<PaymentData, PaymentBodyIntent> {

    private PaymentSettingRepository paymentSettingRepository;
    private UserSelectionRepository userSelectionRepository;
    private DiscountRepository discountRepository;

    public PaymentDataMapper(@NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final UserSelectionRepository userSelectionRepository,
        @NonNull final DiscountRepository discountRepository) {
        this.paymentSettingRepository = paymentSettingRepository;
        this.userSelectionRepository = userSelectionRepository;
        this.discountRepository = discountRepository;
    }

    @Override
    public PaymentBodyIntent map(@NonNull final PaymentData val) {

        //TODO Payer está en PaymentData. Se puede sacar de algún repository?
        final Payer payer = val.getPayer();
        //TODO ver si el email del payer está viniendo bien
        final PaymentBodyIntent.Builder paymentBodyIntentBuilder =
            new PaymentBodyIntent.Builder(paymentSettingRepository.getCheckoutPreferenceId(),
                paymentSettingRepository.getPublicKey(), userSelectionRepository.getPaymentMethod().getId(),
                payer.getEmail());

        paymentBodyIntentBuilder.setBinaryMode(paymentSettingRepository.getCheckoutPreference().isBinaryMode());
        paymentBodyIntentBuilder.setPayer(payer);

        if (paymentSettingRepository.getToken() != null) {
            paymentBodyIntentBuilder.setTokenId(paymentSettingRepository.getToken().getId());
        }
        if (userSelectionRepository.getPayerCost() != null) {
            paymentBodyIntentBuilder.setInstallments(userSelectionRepository.getPayerCost().getInstallments());
        }
        if (userSelectionRepository.getIssuer() != null) {
            paymentBodyIntentBuilder.setIssuerId(userSelectionRepository.getIssuer().getId());
        }
        final Discount discount = discountRepository.getDiscount();
        if (discount != null) {
            paymentBodyIntentBuilder.setCampaignId(discount.getId());
            paymentBodyIntentBuilder.setCouponAmount(discount.getCouponAmount().toString());
            //TODO coupon code está en Payment Data. Se puede sacar de algún repository?
            if (!isEmpty(val.getCouponCode())) {
                paymentBodyIntentBuilder.setCouponCode(val.getCouponCode());
            }
        }

        return paymentBodyIntentBuilder.build();
    }
}
