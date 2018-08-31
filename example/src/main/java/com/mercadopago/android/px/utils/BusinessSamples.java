package com.mercadopago.android.px.utils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import com.mercadopago.SampleTopFragment;
import com.mercadopago.android.px.configuration.PaymentConfiguration;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.internal.features.plugins.SamplePaymentMethodPlugin;
import com.mercadopago.android.px.internal.features.plugins.SamplePaymentProcessor;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.ExitAction;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.example.R;
import java.util.ArrayList;
import java.util.Collection;

final class BusinessSamples {

    private static final String BUTTON_PRIMARY_NAME = "ButtonPrimaryName";
    private static final String BUTTON_SECONDARY_NAME = "ButtonSecondaryName";

    private static final String DUMMY_MERCHANT_PUBLIC_KEY = "TEST-c6d9b1f9-71ff-4e05-9327-3c62468a23ee";
    private static final String DUMMY_PREFERENCE_ID = "243962506-0bb62e22-5c7b-425e-a0a6-c22d0f4758a9";

    private BusinessSamples() {
    }

    public static Collection<? extends Pair<String, MercadoPagoCheckout.Builder>> getAll() {
        final Collection<Pair<String, MercadoPagoCheckout.Builder>> options = new ArrayList<>();
        options.add(new Pair<>("Business - Complete - Rejected", startCompleteRejectedBusiness()));
        options.add(new Pair<>("Business - Complete - Approved", startCompleteApprovedBusiness()));
        options.add(new Pair<>("Business - Primary And Help - Pending", startCompletePendingBusiness()));
        options.add(new Pair<>("Business - No help - Pending", startPendingBusinessNoHelp()));
        options.add(
            new Pair<>("Business - NoHelp w/pm - Approved", startCompleteApprovedBusinessWithPaymentMethodNoHelp()));
        return options;
    }

    private static MercadoPagoCheckout.Builder startCompleteRejectedBusiness() {
        final BusinessPayment payment = getBusinessRejected();
        return customBusinessPayment(payment);
    }

    @NonNull
    public static BusinessPayment getBusinessRejected() {
        final Bundle args = new Bundle();
        args.putParcelable(SampleTopFragment.SOME_PARCELABLE, new SampleTopFragment.ParcelableArgument("SOME_LABEL"));

        return new BusinessPayment.Builder(BusinessPayment.Decorator.REJECTED, Payment.StatusCodes.STATUS_REJECTED,
            Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_CARD_NUMBER,
            R.drawable.px_icon_card, "Title")
            .setHelp("Help description!")
            .setReceiptId("#123455")
            .setTopFragment(SampleTopFragment.class, args)
            .setPaymentMethodVisibility(true)
            .setPrimaryButton(new ExitAction(BUTTON_PRIMARY_NAME, 23))
            .setSecondaryButton(new ExitAction(BUTTON_SECONDARY_NAME, 34))
            .build();
    }

    static MercadoPagoCheckout.Builder startCompleteApprovedBusiness() {
        final Bundle args = new Bundle();
        args.putParcelable(SampleTopFragment.SOME_PARCELABLE, new SampleTopFragment.ParcelableArgument("SOME_LABEL"));
        final BusinessPayment payment = new BusinessPayment.Builder(BusinessPayment.Decorator.APPROVED,
            Payment.StatusCodes.STATUS_APPROVED,
            Payment.StatusDetail.STATUS_DETAIL_ACCREDITED,
            "https://www.jqueryscript.net/images/Simplest-Responsive-jQuery-Image-Lightbox-Plugin-simple-lightbox.jpg",
            "Title")
            .setHelp("Help description!")
            .setReceiptId("#123455")
            .setTopFragment(SampleTopFragment.class, args)
            .setStatementDescription("PEDRO")
            .setPaymentMethodVisibility(true)
            .setSecondaryButton(new ExitAction(BUTTON_SECONDARY_NAME, 34))
            .build();

        return customBusinessPayment(payment);
    }

    private static MercadoPagoCheckout.Builder startCompleteApprovedBusinessWithPaymentMethodNoHelp() {
        final BusinessPayment payment =
            new BusinessPayment.Builder(BusinessPayment.Decorator.APPROVED,
                Payment.StatusCodes.STATUS_APPROVED,
                Payment.StatusDetail.STATUS_DETAIL_ACCREDITED, R.drawable.px_icon_card, "Title")
                .setReceiptId("#123455")
                .setPaymentMethodVisibility(true)
                .setSecondaryButton(new ExitAction(BUTTON_SECONDARY_NAME, 34))
                .build();

        return customBusinessPayment(payment);
    }

    private static MercadoPagoCheckout.Builder startCompletePendingBusiness() {
        final BusinessPayment payment =
            new BusinessPayment.Builder(BusinessPayment.Decorator.PENDING,
                Payment.StatusCodes.STATUS_PENDING,
                Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT,
                R.drawable.px_icon_card, "Title")
                .setHelp("Help description!")
                .setPrimaryButton(new ExitAction(BUTTON_PRIMARY_NAME, 23))
                .build();

        return customBusinessPayment(payment);
    }

    private static MercadoPagoCheckout.Builder startPendingBusinessNoHelp() {
        final BusinessPayment payment =
            new BusinessPayment.Builder(BusinessPayment.Decorator.PENDING,
                Payment.StatusCodes.STATUS_PENDING,
                Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT,
                R.drawable.px_icon_card, "Title")
                .setReceiptId("#123455")
                .setPrimaryButton(new ExitAction(BUTTON_PRIMARY_NAME, 23))
                .setSecondaryButton(new ExitAction(BUTTON_SECONDARY_NAME, 34))
                .build();

        return customBusinessPayment(payment);
    }

    private static MercadoPagoCheckout.Builder customBusinessPayment(final BusinessPayment businessPayment) {
        final SamplePaymentProcessor samplePaymentProcessor = new SamplePaymentProcessor(businessPayment);
        return new MercadoPagoCheckout.Builder(DUMMY_MERCHANT_PUBLIC_KEY, DUMMY_PREFERENCE_ID,
            new PaymentConfiguration.Builder(samplePaymentProcessor)
                .addPaymentMethodPlugin(new SamplePaymentMethodPlugin())
                .build());
    }
}
