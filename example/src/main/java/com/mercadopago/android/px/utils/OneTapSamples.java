package com.mercadopago.android.px.utils;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import com.mercadopago.android.px.configuration.AdvancedConfiguration;
import com.mercadopago.android.px.configuration.DiscountConfiguration;
import com.mercadopago.android.px.configuration.PaymentConfiguration;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.core.PaymentProcessor;
import com.mercadopago.android.px.internal.features.plugins.SamplePaymentProcessorNoView;
import com.mercadopago.android.px.model.GenericPayment;
import com.mercadopago.android.px.model.Item;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.Sites;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.mercadopago.android.px.utils.PaymentUtils.getBusinessPaymentApproved;
import static com.mercadopago.android.px.utils.PaymentUtils.getGenericPaymentApprovedAccountMoney;

public final class OneTapSamples {

    private static final String ONE_TAP_PAYER_1_ACCESS_TOKEN =
        "APP_USR-6519316523937252-070516-964fafa7e2c91a2c740155fcb5474280__LA_LD__-261748045";
    private static final String ONE_TAP_PAYER_2_ACCESS_TOKEN =
        "APP_USR-3456261032857473-073011-c49291f53879f65accfaf28764e55f3e-340764447";
    private static final String ONE_TAP_PAYER_3_ACCESS_TOKEN =
        "TEST-244508097630521-031308-7b8b58d617aec50b3e528ca98606b116__LC_LA__-150216849";
    private static final String ONE_TAP_PAYER_4_ACCESS_TOKEN =
        "APP_USR-3841407354354687-070311-e89f762e2fc6bdb9131c40c58b98f2c4-333082795";
    private static final String ONE_TAP_PAYER_5_ACCESS_TOKEN =
        "APP_USR-7548115878322835-070311-e172a5d11f7f782622163724dbecb9cf-333082950";
    private static final String ONE_TAP_PAYER_6_ACCESS_TOKEN =
        "APP_USR-2962379700180713-073014-662103afe87bd62b4172af7e9599573c-340790299";
    private static final String ONE_TAP_PAYER_7_ACCESS_TOKEN =
        "TEST-7779559135594958-090815-348ca6a8851b34c17bf23a24b19a7b99__LA_LD__-227815697";
    private static final String ONE_TAP_PAYER_8_ACCESS_TOKEN =
        "TEST-1458038826212807-062020-ff9273c67bc567320eae1a07d1c2d5b5-246046416";
    private static final String ONE_TAP_PAYER_9_ACCESS_TOKEN =
        "APP_USR-1031243024729642-070215-4ce0d8f4d71d238fa10c33ac79428e85-332848643";
    private static final String ONE_TAP_MERCHANT_PUBLIC_KEY = "APP_USR-648a260d-6fd9-4ad7-9284-90f22262c18d";
    private static final String ONE_TAP_DIRECT_DISCOUNT_MERCHANT_PUBLIC_KEY =
        "APP_USR-ef65214d-59a2-4c82-be23-6cf6eb945d4c";
    private static final String ONE_TAP_CODE_DISCOUNT_MERCHANT_PUBLIC_KEY =
        "APP_USR-a9fbcb43-4d5a-41c6-a42e-56e8b153f142";
    private static final String PAYER_EMAIL_DUMMY = "prueba@gmail.com";

    private OneTapSamples() {
        //Do nothing
    }

    public static void addAll(final Collection<Pair<String, MercadoPagoCheckout.Builder>> options) {
        options.add(new Pair<>("1 - One tap - Should suggest account money (no cards)",
            startOneTapWithAccountMoneyNoCards()));
        options.add(new Pair<>("2 - One tap - Should suggest account money (debit and credit cards)",
            startOneTapWithAccountMoneyAndCardsDebitCredit()));
        options.add(new Pair<>("3 - One tap - Should suggest debit card (excluded account money)",
            startOneTapWithAccountMoneyAndCardsDebitCreditAndExcludedAccountMoney()));
        options.add(new Pair<>("4 - One tap - Should suggest credit card (excluded account money and debit)",
            startOneTapWithAccountMoneyAndCardsDebitCreditAndExcludedAccountMoneyAndDebit()));
        options.add(new Pair<>("5 - One tap - Should suggest credit card (no account money)",
            startOneTapNoAccountMoneyWithCreditCard()));
        options.add(new Pair<>("6 - One tap - Shouldn't suggest one tap (no cards no account money)",
            startOneTapNoAccountMoneyNoCards()));
        options.add(new Pair<>("7 - One tap - Should suggest credit card (no account money)",
            startOneTapNoAccountMoneyWithCredit()));
        options.add(new Pair<>("8 - One tap - Should suggest credit card (account money with second factor auth",
            startOneTapWithAccountMoneyAndSecondFactorAuthWithCredit()));
        options.add(new Pair<>("9 - One tap - Shouldn't suggest one tap (second factor and excuded credit card)",
            startOneTapWithAccountMoneyAndSecondFactorAuthWithExcludedCreditCard()));
        options.add(new Pair<>("10 - One tap - Should suggest account money (credit card)",
            startOneTapWithAccountMoneyWithCreditCard()));
        options.add(new Pair<>("11 - One tap - Should suggest account money (amount lower than cap)",
            startOneTapWithAccountMoneyLowerThanCap()));
        options.add(new Pair<>("12 - One tap - Shouldn't suggest one tap (amount greater than cap)",
            startOneTapWithAmountGreaterThanCap()));
        options.add(new Pair<>("13 - One tap - Should suggest account money (low account money)",
            startOneTapWithLowAccountMoneyWithLowerAmount()));
        options.add(new Pair<>("14 - One tap - Should suggest credit card (low account money, amount lower than cap)",
            startOneTapWithLowAccountMoneyWithLowerAmountAndLowerCap()));
        options.add(new Pair<>("15 - One tap - Shouldn't suggest one tap (low account money, amount greater than cap)",
            startOneTapWithLowAccountMoneyWithLowerAmountAndGreaterCap()));
        options.add(new Pair<>("16 - One tap - Should suggest credit card (no account money) with direct discount",
            startOneTapNoAccountMoneyWithCreditCardAndDirectDiscount()));
        options.add(new Pair<>("17 - One tap - Should suggest credit card (no account money) with code discount",
            startOneTapNoAccountMoneyWithCreditCardAndCodeDiscount()));
        options
            .add(new Pair<>("18 - One tap - Should suggest credit card (no account money) with not available discount",
                startOneTapNoAccountMoneyWithCreditCardAndNoAvailableDiscount()));
        options.add(new Pair<>("19 - One tap - Should suggest credit card and get call for authorize result",
            startOneTapWithCreditCardAndShowCallForAuthorize()));
    }

    // It should suggest one tap with account money
    private static MercadoPagoCheckout.Builder startOneTapWithAccountMoneyNoCards() {

        final GenericPayment payment = getGenericPaymentApprovedAccountMoney();

        final CheckoutPreference preference =
            getCheckoutPreferenceWithPayerEmail(new ArrayList<String>(), 120);
        final PaymentConfiguration paymentConfiguration =
            PaymentConfigurationUtils.createWithPlugin(new SamplePaymentProcessorNoView(payment));

        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY, preference, paymentConfiguration)
            .setPrivateKey(ONE_TAP_PAYER_1_ACCESS_TOKEN);
    }

    // It should suggest one tap with account money
    private static MercadoPagoCheckout.Builder startOneTapWithAccountMoneyAndCardsDebitCredit() {

        final GenericPayment payment = getGenericPaymentApprovedAccountMoney();
        final PaymentProcessor samplePaymentProcessor = new SamplePaymentProcessorNoView(payment);
        final CheckoutPreference preference = getCheckoutPreferenceWithPayerEmail(120);
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY, preference,
            PaymentConfigurationUtils
                .createWithPlugin(samplePaymentProcessor))
            .setPrivateKey(ONE_TAP_PAYER_2_ACCESS_TOKEN);
    }

    // It should suggest one tap with debit card
    private static MercadoPagoCheckout.Builder startOneTapWithAccountMoneyAndCardsDebitCreditAndExcludedAccountMoney() {
        final GenericPayment payment = new GenericPayment(123L, Payment.StatusCodes.STATUS_APPROVED,
            Payment.StatusDetail.STATUS_DETAIL_ACCREDITED);
        final PaymentProcessor samplePaymentProcessor = new SamplePaymentProcessorNoView(payment);
        final Collection<String> excludedPaymentTypes = new ArrayList<>();
        excludedPaymentTypes.add("account_money");
        final CheckoutPreference checkoutPreferenceWithPayerEmail =
            getCheckoutPreferenceWithPayerEmail(excludedPaymentTypes, 120);
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY, checkoutPreferenceWithPayerEmail,
            PaymentConfigurationUtils.createWithPlugin(samplePaymentProcessor))
            .setPrivateKey(ONE_TAP_PAYER_2_ACCESS_TOKEN);
    }

    // It should suggest one tap with credit card
    private static MercadoPagoCheckout.Builder startOneTapWithAccountMoneyAndCardsDebitCreditAndExcludedAccountMoneyAndDebit() {
        final GenericPayment payment = new GenericPayment(123L, Payment.StatusCodes.STATUS_APPROVED,
            Payment.StatusDetail.STATUS_DETAIL_ACCREDITED);
        final PaymentProcessor samplePaymentProcessor = new SamplePaymentProcessorNoView(payment);
        final Collection<String> excludedPaymentTypes = new ArrayList<>();
        excludedPaymentTypes.add("account_money");
        excludedPaymentTypes.add("debit_card");
        final CheckoutPreference checkoutPreferenceWithPayerEmail =
            getCheckoutPreferenceWithPayerEmail(excludedPaymentTypes, 120);
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY, checkoutPreferenceWithPayerEmail,
            PaymentConfigurationUtils.createWithPlugin(samplePaymentProcessor))
            .setAdvancedConfiguration(new AdvancedConfiguration.Builder().setEscEnabled(true).build())
            .setPrivateKey(ONE_TAP_PAYER_2_ACCESS_TOKEN);
    }

    // It should suggest one tap with credit card
    private static MercadoPagoCheckout.Builder startOneTapNoAccountMoneyWithCreditCard() {
        final PaymentProcessor samplePaymentProcessor = new SamplePaymentProcessorNoView(getBusinessPaymentApproved());
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY, getCheckoutPreferenceWithPayerEmail(120),
            PaymentConfigurationUtils
                .createWithPlugin(
                samplePaymentProcessor))
            .setPrivateKey(ONE_TAP_PAYER_3_ACCESS_TOKEN);
    }

    // It shouldn't suggest one tap
    private static MercadoPagoCheckout.Builder startOneTapNoAccountMoneyNoCards() {
        final PaymentProcessor samplePaymentProcessor = new SamplePaymentProcessorNoView(getBusinessPaymentApproved());
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY, getCheckoutPreferenceWithPayerEmail(120),
            PaymentConfigurationUtils
                .createWithPlugin(
                samplePaymentProcessor))
            .setPrivateKey(ONE_TAP_PAYER_4_ACCESS_TOKEN);
    }

    // It should suggest one tap with credit card
    private static MercadoPagoCheckout.Builder startOneTapNoAccountMoneyWithCredit() {
        final PaymentProcessor samplePaymentProcessor = new SamplePaymentProcessorNoView(getBusinessPaymentApproved());
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY, getCheckoutPreferenceWithPayerEmail(120),
            PaymentConfigurationUtils
                .createWithPlugin(
                samplePaymentProcessor))
            .setPrivateKey(ONE_TAP_PAYER_5_ACCESS_TOKEN);
    }

    // It should suggest one tap with credit card
    private static MercadoPagoCheckout.Builder startOneTapWithAccountMoneyAndSecondFactorAuthWithCredit() {

        final PaymentProcessor samplePaymentProcessor = new SamplePaymentProcessorNoView(getBusinessPaymentApproved());
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY,
            getCheckoutPreferenceWithPayerEmail(120),
            PaymentConfigurationUtils
                .createWithPlugin(
                    samplePaymentProcessor))
            .setPrivateKey(ONE_TAP_PAYER_6_ACCESS_TOKEN);
    }

    // It shouldn't suggest one tap
    private static MercadoPagoCheckout.Builder startOneTapWithAccountMoneyAndSecondFactorAuthWithExcludedCreditCard() {
        final PaymentProcessor samplePaymentProcessor = new SamplePaymentProcessorNoView(getBusinessPaymentApproved());
        final Collection<String> excludedPaymentTypes = new ArrayList<>();
        excludedPaymentTypes.add("credit_card");
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY,
            getCheckoutPreferenceWithPayerEmail(excludedPaymentTypes, 120), PaymentConfigurationUtils
            .createWithPlugin(
                samplePaymentProcessor))
            .setPrivateKey(ONE_TAP_PAYER_6_ACCESS_TOKEN);
    }

    // It should suggest one tap with acount money
    private static MercadoPagoCheckout.Builder startOneTapWithAccountMoneyWithCreditCard() {
        final PaymentProcessor samplePaymentProcessor = new SamplePaymentProcessorNoView(getBusinessPaymentApproved());
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY, getCheckoutPreferenceWithPayerEmail(120),
            PaymentConfigurationUtils
                .createWithPlugin(
                    samplePaymentProcessor))
            .setPrivateKey(ONE_TAP_PAYER_7_ACCESS_TOKEN);
    }

    // It should suggest one tap with acount money
    private static MercadoPagoCheckout.Builder startOneTapWithAccountMoneyLowerThanCap() {
        final PaymentProcessor samplePaymentProcessor = new SamplePaymentProcessorNoView(getBusinessPaymentApproved());
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY,
            getCheckoutPreferenceWithPayerEmail(120),
            PaymentConfigurationUtils
                .createWithPlugin(
                    samplePaymentProcessor))
            .setPrivateKey(ONE_TAP_PAYER_8_ACCESS_TOKEN);
    }

    // It shouldn't suggest one tap
    private static MercadoPagoCheckout.Builder startOneTapWithAmountGreaterThanCap() {
        final PaymentProcessor samplePaymentProcessor = new SamplePaymentProcessorNoView(getBusinessPaymentApproved());
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY, getCheckoutPreferenceWithPayerEmail(800),
            PaymentConfigurationUtils
                .createWithPlugin(
                samplePaymentProcessor))
            .setPrivateKey(ONE_TAP_PAYER_8_ACCESS_TOKEN);
    }

    // It should suggest one tap with acount money
    private static MercadoPagoCheckout.Builder startOneTapWithLowAccountMoneyWithLowerAmount() {

        final PaymentProcessor samplePaymentProcessor = new SamplePaymentProcessorNoView(getBusinessPaymentApproved());
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY,
            getCheckoutPreferenceWithPayerEmail(120),
            PaymentConfigurationUtils
                .createWithPlugin(
                    samplePaymentProcessor))
            .setPrivateKey(ONE_TAP_PAYER_9_ACCESS_TOKEN);
    }

    // It should suggest one tap with credit card
    private static MercadoPagoCheckout.Builder startOneTapWithLowAccountMoneyWithLowerAmountAndLowerCap() {

        final PaymentProcessor samplePaymentProcessor = new SamplePaymentProcessorNoView(getBusinessPaymentApproved());
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY,
            getCheckoutPreferenceWithPayerEmail(500),
            PaymentConfigurationUtils
                .createWithPlugin(
                    samplePaymentProcessor))
            .setPrivateKey(ONE_TAP_PAYER_9_ACCESS_TOKEN);
    }

    // It shouldn't suggest one tap
    private static MercadoPagoCheckout.Builder startOneTapWithLowAccountMoneyWithLowerAmountAndGreaterCap() {
        final PaymentProcessor samplePaymentProcessor = new SamplePaymentProcessorNoView(getBusinessPaymentApproved());
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY, getCheckoutPreferenceWithPayerEmail(701),
            PaymentConfigurationUtils
                .createWithPlugin(
                    samplePaymentProcessor))
            .setPrivateKey(ONE_TAP_PAYER_9_ACCESS_TOKEN);
    }

    // It should suggest one tap with credit card
    private static MercadoPagoCheckout.Builder startOneTapNoAccountMoneyWithCreditCardAndDirectDiscount() {
        return new MercadoPagoCheckout.Builder(ONE_TAP_DIRECT_DISCOUNT_MERCHANT_PUBLIC_KEY,
            getCheckoutPreferenceWithPayerEmail(120),
            PaymentConfigurationUtils.create())
            .setPrivateKey(ONE_TAP_PAYER_3_ACCESS_TOKEN);
    }

    // It should suggest one tap with credit card and not available discount
    private static MercadoPagoCheckout.Builder startOneTapNoAccountMoneyWithCreditCardAndNoAvailableDiscount() {
        final SamplePaymentProcessorNoView samplePaymentProcessor = new SamplePaymentProcessorNoView(getBusinessPaymentApproved());
        final CheckoutPreference preference = getCheckoutPreferenceWithPayerEmail(new ArrayList<String>(), 120);
        return new MercadoPagoCheckout.Builder(ONE_TAP_DIRECT_DISCOUNT_MERCHANT_PUBLIC_KEY, preference,
            new PaymentConfiguration.Builder(samplePaymentProcessor)
                .setDiscountConfiguration(DiscountConfiguration.forNotAvailableDiscount()).build())
            .setPrivateKey(ONE_TAP_PAYER_3_ACCESS_TOKEN);
    }

    // It should suggest one tap with credit card
    private static MercadoPagoCheckout.Builder startOneTapNoAccountMoneyWithCreditCardAndCodeDiscount() {
        final CheckoutPreference preference =
            getCheckoutPreferenceWithPayerEmail(120);
        return new MercadoPagoCheckout.Builder(ONE_TAP_CODE_DISCOUNT_MERCHANT_PUBLIC_KEY, preference,
            PaymentConfigurationUtils.create())
            .setPrivateKey(ONE_TAP_PAYER_3_ACCESS_TOKEN);
    }

    // It should suggest one tap with credit card, call for authorize
    private static MercadoPagoCheckout.Builder startOneTapWithCreditCardAndShowCallForAuthorize() {
        final GenericPayment payment = new GenericPayment(123L, Payment.StatusCodes.STATUS_REJECTED,
            Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);
        final PaymentProcessor samplePaymentProcessor = new SamplePaymentProcessorNoView(payment);
        final Collection<String> excludedPaymentTypes = new ArrayList<>();
        excludedPaymentTypes.add("account_money");
        excludedPaymentTypes.add("debit_card");
        final CheckoutPreference checkoutPreferenceWithPayerEmail =
            getCheckoutPreferenceWithPayerEmail(excludedPaymentTypes, 120);
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY, checkoutPreferenceWithPayerEmail,
            PaymentConfigurationUtils.createWithPlugin(samplePaymentProcessor))
            .setAdvancedConfiguration(new AdvancedConfiguration.Builder().setEscEnabled(true).build())
            .setPrivateKey(ONE_TAP_PAYER_2_ACCESS_TOKEN);
    }

    private static CheckoutPreference getCheckoutPreferenceWithPayerEmail(final int amount) {
        return getCheckoutPreferenceWithPayerEmail(new ArrayList<String>(), amount);
    }

    private static CheckoutPreference getCheckoutPreferenceWithPayerEmail(
        @NonNull final Collection<String> excludedPaymentTypes, final int amount) {
        final List<Item> items = new ArrayList<>();
        final Item item =
            new Item.Builder("Product title", 1, new BigDecimal(amount))
                .build();
        items.add(item);
        return new CheckoutPreference.Builder(Sites.ARGENTINA,
            PAYER_EMAIL_DUMMY, items)
            .addExcludedPaymentTypes(excludedPaymentTypes)
            .build();
    }
}
