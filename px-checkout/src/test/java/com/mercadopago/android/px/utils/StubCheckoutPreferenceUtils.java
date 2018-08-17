package com.mercadopago.android.px.utils;

import com.mercadopago.android.px.model.Item;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.Sites;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public final class StubCheckoutPreferenceUtils {

    private StubCheckoutPreferenceUtils() {
    }

    public static CheckoutPreference stubPreferenceWithSomePaymentTypesExcluded() {
        return stubBuilderOneItemAndPayer()
            .addExcludedPaymentType(PaymentTypes.CREDIT_CARD)
            .addExcludedPaymentType(PaymentTypes.DEBIT_CARD)
            .addExcludedPaymentType(PaymentTypes.PREPAID_CARD)
            .build();
    }

    public static CheckoutPreference stubPreferenceWithPositiveMaxInstallmentsNumberAndNegativeDefaultInstallmentsNumber() {
        return stubBuilderOneItemAndPayer()
            .setMaxInstallments(1)
            .setDefaultInstallments(-3)
            .build();
    }

    public static CheckoutPreference stubPreferenceWithPositiveDefaultInstallmentsNumberAndNegativeMaxInstallmentsNumber() {
        return stubBuilderOneItemAndPayer()
            .setMaxInstallments(-1)
            .setDefaultInstallments(3)
            .build();
    }

    public static CheckoutPreference stubPreferenceWithPositiveInstallmentsNumber() {
        return stubBuilderOneItemAndPayer()
            .setMaxInstallments(1)
            .setDefaultInstallments(3)
            .build();
    }

    public static CheckoutPreference stubPreferenceWithNegativeInstallmentsNumbers() {
        return stubBuilderOneItemAndPayer()
            .setMaxInstallments(-1)
            .setDefaultInstallments(-1)
            .build();
    }

    public static CheckoutPreference stubPreferenceWithOneItemValidActiveAndSomePaymentTypesExcluded() {
        Date pastDate = new Date();
        pastDate.setTime((new Date().getTime()) - 1000 * 60 * 60);
        return stubBuilderOneItemAndPayer()
            .addExcludedPaymentType(PaymentTypes.CREDIT_CARD)
            .addExcludedPaymentType(PaymentTypes.DEBIT_CARD)
            .addExcludedPaymentType(PaymentTypes.PREPAID_CARD)
            .setActiveFrom(pastDate)
            .setMaxInstallments(1)
            .setDefaultInstallments(1)
            .build();
    }

    public static CheckoutPreference stubPreferenceWithOneItemValidActiveButAllPaymentTypesExcluded() {
        Date pastDate = new Date();
        pastDate.setTime((new Date().getTime()) - 1000 * 60 * 60);
        return stubBuilderOneItemAndPayer()
            .addExcludedPaymentTypes(PaymentTypes.getAllPaymentTypes())
            .setActiveFrom(pastDate)
            .build();
    }

    public static CheckoutPreference stubPreferenceWithOneItemValidButInstallmentsDefaultNumberAndInstallmentsNumberNegative() {
        Date pastDate = new Date();
        pastDate.setTime((new Date().getTime()) - 1000 * 60 * 60);
        return stubBuilderOneItemAndPayer()
            .setMaxInstallments(-1)
            .setDefaultInstallments(-3)
            .setActiveFrom(pastDate)
            .build();
    }

    public static CheckoutPreference stubPreferenceWithAllPaymentTypesExcluded() {
        ArrayList<String> paymentTypes = new ArrayList<>(PaymentTypes.getAllPaymentTypes());
        return stubBuilderOneItemAndPayer()
            .addExcludedPaymentTypes(paymentTypes)
            .build();
    }

    public static CheckoutPreference stubInactivePreferenceAndPayer() {
        GregorianCalendar calendar = new GregorianCalendar(2100, 3, 3); //Date should be after that today
        Date date = calendar.getTime();
        return stubBuilderOneItemAndPayer()
            .setActiveFrom(date)
            .build();
    }

    public static CheckoutPreference stubActivePreferenceAndPayer() {
        Date pastDate = new Date();
        pastDate.setTime((new Date().getTime()) - 1000 * 60 * 60);
        return stubBuilderOneItemAndPayer()
            .setActiveFrom(pastDate)
            .build();
    }

    public static CheckoutPreference stubPreferenceOneItemAndPayer() {
        return stubBuilderOneItemAndPayer().build();
    }

    public static CheckoutPreference.Builder stubBuilderOneItemAndPayer() {
        return stubBuilderOneItem();
    }

    public static CheckoutPreference stubPreferenceOneItem() {
        return stubBuilderOneItem()
            .build();
    }

    private static CheckoutPreference.Builder stubBuilderOneItem() {
        return new CheckoutPreference.Builder(Sites.ARGENTINA, "unemail@gmail.com", stubOneItemList());
    }

    public static CheckoutPreference stubExpiredPreference() {
        GregorianCalendar calendar = new GregorianCalendar(2015, 3, 3); //Date should be before that today
        Date date = calendar.getTime();
        return stubBuilderOneItemAndPayer()
            .setExpirationDate(date)
            .build();
    }

    private static List<Item> stubOneItemList() {
        Item dummy = new Item.Builder("Dummy item", 1, BigDecimal.TEN).setDescription("description").build();
        return Collections.singletonList(dummy);
    }
}
