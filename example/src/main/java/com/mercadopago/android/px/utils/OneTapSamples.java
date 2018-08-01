package com.mercadopago.android.px.utils;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.model.Item;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.Sites;
import com.mercadopago.android.px.plugins.MainPaymentProcessor;
import com.mercadopago.android.px.plugins.SamplePaymentMethodPlugin;
import com.mercadopago.android.px.plugins.model.BusinessPayment;
import com.mercadopago.android.px.plugins.model.ExitAction;
import com.mercadopago.android.px.plugins.model.GenericPayment;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.example.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    private static final String ONE_TAP_DIRECT_DISCOUNT_MERCHANT_PUBLIC_KEY = "APP_USR-ef65214d-59a2-4c82-be23-6cf6eb945d4c";
    private static final String ONE_TAP_CODE_DISCOUNT_MERCHANT_PUBLIC_KEY = "APP_USR-a9fbcb43-4d5a-41c6-a42e-56e8b153f142";
    private static final String PAYER_EMAIL_DUMMY = "prueba@gmail.com";

    private OneTapSamples() {

    }

    public static void addAll(final Collection<Pair<String, MercadoPagoCheckout.Builder>> options) {
        options.add(
                new Pair<>("1 - One tap - Should suggest account money (no cards)", startOneTapWithAccountMoneyNoCards()));
        options
                .add(new Pair<>("2 - One tap - Should suggest account money (debit and credit cards)",
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
                starOneTapWithAmountGreaterThanCap()));
        options.add(new Pair<>("13 - One tap - Should suggest account money (low account money)",
                starOneTapWithLowAccountMoneyWithLowerAmount()));
        options.add(new Pair<>("14 - One tap - Should suggest credit card (low account money, amount lower than cap)",
                starOneTapWithLowAccountMoneyWithLowerAmountAndLowerCap()));
        options.add(new Pair<>("15 - One tap - Shouldn't suggest one tap (low account money, amount greater than cap)",
                starOneTapWithLowAccountMoneyWithLowerAmountAndGreaterCap()));
        options.add(new Pair<>("16 - One tap - Should suggest credit card (no account money) with direct discount",
                startOneTapNoAccountMoneyWithCreditCardAndDirectDiscount()));
        options.add(new Pair<>("17 - One tap - Should suggest credit card (no account money) with code discount",
                startOneTapNoAccountMoneyWithCreditCardAndCodeDiscount()));
    }

    // It should suggest one tap with account money
    private static MercadoPagoCheckout.Builder startOneTapWithAccountMoneyNoCards() {

        final GenericPayment payment = new GenericPayment(123L, Payment.StatusCodes.STATUS_APPROVED,
                Payment.StatusDetail.STATUS_DETAIL_ACCREDITED, getPaymentDataWithAccountMoneyPlugin(new BigDecimal(120)));
        final MainPaymentProcessor mainPaymentProcessor = new MainPaymentProcessor(payment);
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY,
                getCheckoutPreferenceWithPayerEmail(new ArrayList<String>(), 120))
                .setPaymentProcessor(mainPaymentProcessor)
                .addPaymentMethodPlugin(new SamplePaymentMethodPlugin(), mainPaymentProcessor)
                .setPrivateKey(ONE_TAP_PAYER_1_ACCESS_TOKEN);
    }

    // It should suggest one tap with account money
    private static MercadoPagoCheckout.Builder startOneTapWithAccountMoneyAndCardsDebitCredit() {

        final GenericPayment payment = new GenericPayment(123L, Payment.StatusCodes.STATUS_APPROVED,
                Payment.StatusDetail.STATUS_DETAIL_ACCREDITED, getPaymentDataWithAccountMoneyPlugin(new BigDecimal(120)));
        final MainPaymentProcessor mainPaymentProcessor = new MainPaymentProcessor(payment);

        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY,
                getCheckoutPreferenceWithPayerEmail(new ArrayList<String>(), 120))
                .setPaymentProcessor(mainPaymentProcessor)
                .addPaymentMethodPlugin(new SamplePaymentMethodPlugin(), mainPaymentProcessor)
                .setPrivateKey(ONE_TAP_PAYER_2_ACCESS_TOKEN);
    }

    // It should suggest one tap with debit card
    private static MercadoPagoCheckout.Builder startOneTapWithAccountMoneyAndCardsDebitCreditAndExcludedAccountMoney() {
        final GenericPayment payment = new GenericPayment(123L, Payment.StatusCodes.STATUS_APPROVED,
                Payment.StatusDetail.STATUS_DETAIL_ACCREDITED, getPaymentDataWithDebitCardMaster(new BigDecimal(120)));
        final MainPaymentProcessor mainPaymentProcessor = new MainPaymentProcessor(payment);
        final List<String> excludedPaymentTypes = new ArrayList<>();
        excludedPaymentTypes.add("account_money");
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY,
                getCheckoutPreferenceWithPayerEmail(excludedPaymentTypes, 120))
                .setPaymentProcessor(mainPaymentProcessor)
                .addPaymentMethodPlugin(new SamplePaymentMethodPlugin(), mainPaymentProcessor)
                .setPrivateKey(ONE_TAP_PAYER_2_ACCESS_TOKEN);
    }

    // It should suggest one tap with credit card
    private static MercadoPagoCheckout.Builder startOneTapWithAccountMoneyAndCardsDebitCreditAndExcludedAccountMoneyAndDebit() {
        final GenericPayment payment = new GenericPayment(123L, Payment.StatusCodes.STATUS_APPROVED,
                Payment.StatusDetail.STATUS_DETAIL_ACCREDITED, getPaymentDataWithCreditCardNaranja(new BigDecimal(120)));
        final MainPaymentProcessor mainPaymentProcessor = new MainPaymentProcessor(payment);
        final List<String> excludedPaymentTypes = new ArrayList<>();
        excludedPaymentTypes.add("account_money");
        excludedPaymentTypes.add("debit_card");
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY,
                getCheckoutPreferenceWithPayerEmail(excludedPaymentTypes, 120))
                .setPaymentProcessor(mainPaymentProcessor)
                .addPaymentMethodPlugin(new SamplePaymentMethodPlugin(), mainPaymentProcessor)
                .setPrivateKey(ONE_TAP_PAYER_2_ACCESS_TOKEN);
    }

    // It should suggest one tap with credit card
    private static MercadoPagoCheckout.Builder startOneTapNoAccountMoneyWithCreditCard() {

        final MainPaymentProcessor mainPaymentProcessor = new MainPaymentProcessor(getBusinessPaymentApproved());
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY,
                getCheckoutPreferenceWithPayerEmail(new ArrayList<String>(), 120))
                .setPaymentProcessor(mainPaymentProcessor)
                .addPaymentMethodPlugin(new SamplePaymentMethodPlugin(), mainPaymentProcessor)
                .setPrivateKey(ONE_TAP_PAYER_3_ACCESS_TOKEN);
    }

    // It shouldn't suggest one tap
    private static MercadoPagoCheckout.Builder startOneTapNoAccountMoneyNoCards() {

        final MainPaymentProcessor mainPaymentProcessor = new MainPaymentProcessor(getBusinessPaymentApproved());
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY,
                getCheckoutPreferenceWithPayerEmail(new ArrayList<String>(), 120))
                .setPaymentProcessor(mainPaymentProcessor)
                .addPaymentMethodPlugin(new SamplePaymentMethodPlugin(), mainPaymentProcessor)
                .setPrivateKey(ONE_TAP_PAYER_4_ACCESS_TOKEN);
    }

    // It should suggest one tap with credit card
    private static MercadoPagoCheckout.Builder startOneTapNoAccountMoneyWithCredit() {

        final MainPaymentProcessor mainPaymentProcessor = new MainPaymentProcessor(getBusinessPaymentApproved());
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY,
                getCheckoutPreferenceWithPayerEmail(new ArrayList<String>(), 120))
                .setPaymentProcessor(mainPaymentProcessor)
                .addPaymentMethodPlugin(new SamplePaymentMethodPlugin(), mainPaymentProcessor)
                .setPrivateKey(ONE_TAP_PAYER_5_ACCESS_TOKEN);
    }

    // It should suggest one tap with credit card
    private static MercadoPagoCheckout.Builder startOneTapWithAccountMoneyAndSecondFactorAuthWithCredit() {

        final MainPaymentProcessor mainPaymentProcessor = new MainPaymentProcessor(getBusinessPaymentApproved());
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY,
                getCheckoutPreferenceWithPayerEmail(new ArrayList<String>(), 120))
                .setPaymentProcessor(mainPaymentProcessor)
                .addPaymentMethodPlugin(new SamplePaymentMethodPlugin(), mainPaymentProcessor)
                .setPrivateKey(ONE_TAP_PAYER_6_ACCESS_TOKEN);
    }

    // It shouldn't suggest one tap
    private static MercadoPagoCheckout.Builder startOneTapWithAccountMoneyAndSecondFactorAuthWithExcludedCreditCard() {

        final MainPaymentProcessor mainPaymentProcessor = new MainPaymentProcessor(getBusinessPaymentApproved());
        final List<String> excludedPaymentTypes = new ArrayList<>();
        excludedPaymentTypes.add("credit_card");
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY,
                getCheckoutPreferenceWithPayerEmail(excludedPaymentTypes, 120))
                .setPaymentProcessor(mainPaymentProcessor)
                .addPaymentMethodPlugin(new SamplePaymentMethodPlugin(), mainPaymentProcessor)
                .setPrivateKey(ONE_TAP_PAYER_6_ACCESS_TOKEN);
    }

    // It should suggest one tap with acount money
    private static MercadoPagoCheckout.Builder startOneTapWithAccountMoneyWithCreditCard() {

        final MainPaymentProcessor mainPaymentProcessor = new MainPaymentProcessor(getBusinessPaymentApproved());
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY,
                getCheckoutPreferenceWithPayerEmail(new ArrayList<String>(), 120))
                .setPaymentProcessor(mainPaymentProcessor)
                .addPaymentMethodPlugin(new SamplePaymentMethodPlugin(), mainPaymentProcessor)
                .setPrivateKey(ONE_TAP_PAYER_7_ACCESS_TOKEN);
    }

    // It should suggest one tap with acount money
    private static MercadoPagoCheckout.Builder startOneTapWithAccountMoneyLowerThanCap() {

        final MainPaymentProcessor mainPaymentProcessor = new MainPaymentProcessor(getBusinessPaymentApproved());
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY,
                getCheckoutPreferenceWithPayerEmail(new ArrayList<String>(), 120))
                .setPaymentProcessor(mainPaymentProcessor)
                .addPaymentMethodPlugin(new SamplePaymentMethodPlugin(), mainPaymentProcessor)
                .setPrivateKey(ONE_TAP_PAYER_8_ACCESS_TOKEN);
    }

    // It shouldn't suggest one tap
    private static MercadoPagoCheckout.Builder starOneTapWithAmountGreaterThanCap() {

        final MainPaymentProcessor mainPaymentProcessor = new MainPaymentProcessor(getBusinessPaymentApproved());
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY,
                getCheckoutPreferenceWithPayerEmail(new ArrayList<String>(), 800))
                .setPaymentProcessor(mainPaymentProcessor)
                .addPaymentMethodPlugin(new SamplePaymentMethodPlugin(), mainPaymentProcessor)
                .setPrivateKey(ONE_TAP_PAYER_8_ACCESS_TOKEN);
    }

    // It should suggest one tap with acount money
    private static MercadoPagoCheckout.Builder starOneTapWithLowAccountMoneyWithLowerAmount() {

        final MainPaymentProcessor mainPaymentProcessor = new MainPaymentProcessor(getBusinessPaymentApproved());
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY,
                getCheckoutPreferenceWithPayerEmail(new ArrayList<String>(), 120))
                .setPaymentProcessor(mainPaymentProcessor)
                .addPaymentMethodPlugin(new SamplePaymentMethodPlugin(), mainPaymentProcessor)
                .setPrivateKey(ONE_TAP_PAYER_9_ACCESS_TOKEN);
    }

    // It should suggest one tap with credit card
    private static MercadoPagoCheckout.Builder starOneTapWithLowAccountMoneyWithLowerAmountAndLowerCap() {

        final MainPaymentProcessor mainPaymentProcessor = new MainPaymentProcessor(getBusinessPaymentApproved());
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY,
                getCheckoutPreferenceWithPayerEmail(new ArrayList<String>(), 500))
                .setPaymentProcessor(mainPaymentProcessor)
                .addPaymentMethodPlugin(new SamplePaymentMethodPlugin(), mainPaymentProcessor)
                .setPrivateKey(ONE_TAP_PAYER_9_ACCESS_TOKEN);
    }

    // It shouldn't suggest one tap
    private static MercadoPagoCheckout.Builder starOneTapWithLowAccountMoneyWithLowerAmountAndGreaterCap() {
        final MainPaymentProcessor mainPaymentProcessor = new MainPaymentProcessor(getBusinessPaymentApproved());
        return new MercadoPagoCheckout.Builder(ONE_TAP_MERCHANT_PUBLIC_KEY,
                getCheckoutPreferenceWithPayerEmail(new ArrayList<String>(), 701))
                .setPaymentProcessor(mainPaymentProcessor)
                .addPaymentMethodPlugin(new SamplePaymentMethodPlugin(), mainPaymentProcessor)
                .setPrivateKey(ONE_TAP_PAYER_9_ACCESS_TOKEN);
    }

    // It should suggest one tap with credit card
    private static MercadoPagoCheckout.Builder startOneTapNoAccountMoneyWithCreditCardAndDirectDiscount() {

        return new MercadoPagoCheckout.Builder(ONE_TAP_DIRECT_DISCOUNT_MERCHANT_PUBLIC_KEY,
                getCheckoutPreferenceWithPayerEmail(new ArrayList<String>(), 120))
                .setPrivateKey(ONE_TAP_PAYER_3_ACCESS_TOKEN);
    }

    // It should suggest one tap with credit card
    private static MercadoPagoCheckout.Builder startOneTapNoAccountMoneyWithCreditCardAndCodeDiscount() {

        return new MercadoPagoCheckout.Builder(ONE_TAP_CODE_DISCOUNT_MERCHANT_PUBLIC_KEY,
                getCheckoutPreferenceWithPayerEmail(new ArrayList<String>(), 120))
                .setPrivateKey(ONE_TAP_PAYER_3_ACCESS_TOKEN);
    }

    private static BusinessPayment getBusinessPaymentApproved() {
        return new BusinessPayment.Builder(BusinessPayment.Decorator.APPROVED, Payment.StatusCodes.STATUS_APPROVED,
                Payment.StatusDetail.STATUS_DETAIL_ACCREDITED,
                R.drawable.px_icon_card, "Title")
                .setPrimaryButton(new ExitAction("Button Name", 23))
                .build();
    }

    private static PaymentData getPaymentDataWithAccountMoneyPlugin(final BigDecimal amount) {
        final PaymentData paymentData = new PaymentData();
        final PaymentMethod paymentMethod = new PaymentMethod("account_money", "Dinero en cuenta", "account_money");
        paymentData.setPaymentMethod(paymentMethod);
        paymentData.setTransactionAmount(amount);
        return paymentData;
    }

    private static PaymentData getPaymentDataWithDebitCardMaster(final BigDecimal amount) {
        final PaymentData paymentData = new PaymentData();
        final PaymentMethod paymentMethod = new PaymentMethod("debmaster", "Mastercard Débito", "debit_card");
        paymentData.setPaymentMethod(paymentMethod);
        paymentData.setTransactionAmount(amount);
        return paymentData;
    }

    private static PaymentData getPaymentDataWithCreditCardNaranja(final BigDecimal amount) {
        final PaymentData paymentData = new PaymentData();
        final PaymentMethod paymentMethod = new PaymentMethod("naranja", "Naranja", "credit_card");
        paymentData.setPaymentMethod(paymentMethod);
        paymentData.setTransactionAmount(amount);
        return paymentData;
    }

    private static CheckoutPreference getCheckoutPreferenceWithPayerEmail(
            @NonNull final List<String> excludedPaymentTypes, int amount) {
        final List<Item> items = new ArrayList<>();
        final Item item = new Item("description", 1, new BigDecimal(amount));
        item.setTitle("Titulo del item");
        item.setId("1234");
        item.setCurrencyId(Sites.ARGENTINA.getCurrencyId());
        items.add(item);
        final CheckoutPreference checkoutPreference = new CheckoutPreference.Builder(Sites.ARGENTINA,
                PAYER_EMAIL_DUMMY, items)
                .addExcludedPaymentTypes(excludedPaymentTypes)
                .build();
        return checkoutPreference;
    }
}
