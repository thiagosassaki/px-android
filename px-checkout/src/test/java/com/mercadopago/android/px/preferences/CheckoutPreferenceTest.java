package com.mercadopago.android.px.preferences;

import com.mercadopago.android.px.model.exceptions.CheckoutPreferenceException;
import org.junit.Test;

import static com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubActivePreferenceAndPayer;
import static com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubBuilderOneItemAndPayer;
import static com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubExpiredPreference;
import static com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubInactivePreferenceAndPayer;
import static com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubPreferenceWithAllPaymentTypesExcluded;
import static com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubPreferenceWithNegativeInstallmentsNumbers;
import static com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubPreferenceWithOneItemValidActiveAndSomePaymentTypesExcluded;
import static com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubPreferenceWithOneItemValidActiveButAllPaymentTypesExcluded;
import static com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubPreferenceWithOneItemValidButInstallmentsDefaultNumberAndInstallmentsNumberNegative;
import static com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubPreferenceWithPositiveDefaultInstallmentsNumberAndNegativeMaxInstallmentsNumber;
import static com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubPreferenceWithPositiveInstallmentsNumber;
import static com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubPreferenceWithPositiveMaxInstallmentsNumberAndNegativeDefaultInstallmentsNumber;
import static com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubPreferenceWithSomePaymentTypesExcluded;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class CheckoutPreferenceTest {

    ///////////////////PAYMENTS_TYPES tests///////////////////
    @Test
    public void testWhenValidatePreferenceWithAllPaymentsTypesExcludedReturnFalse() {
        CheckoutPreference preference = stubPreferenceWithAllPaymentTypesExcluded();
        assertFalse(preference.validPaymentTypeExclusion());
    }

    @Test
    public void testWhenValidatePreferenceWithSomePaymentsTypesExcludedReturnTrue() {
        CheckoutPreference preference = stubPreferenceWithSomePaymentTypesExcluded();
        assertTrue(preference.validPaymentTypeExclusion());
    }

    ///////////////////INSTALLMENTS tests///////////////////
    @Test
    public void testWhenValidatePreferenceWithPositiveDefaultInstallmentsNumberAndNegativeMaxInstallmentsNumberReturnFalse() {
        CheckoutPreference preference =
            stubPreferenceWithPositiveDefaultInstallmentsNumberAndNegativeMaxInstallmentsNumber();
        assertFalse(preference.validInstallmentsPreference());
    }

    @Test
    public void testWhenValidatePreferenceWithPositiveMaxInstallmentsNumberAndNegativeDefaultInstallmentsNumberReturnFalse() {
        CheckoutPreference preference =
            stubPreferenceWithPositiveMaxInstallmentsNumberAndNegativeDefaultInstallmentsNumber();
        assertFalse(preference.validInstallmentsPreference());
    }

    @Test
    public void testWhenValidatePreferenceWithMaxInstallmentsNumberPositiveReturnTrue() {
        CheckoutPreference preference = stubPreferenceWithPositiveInstallmentsNumber();
        assertTrue(preference.validInstallmentsPreference());
    }

    @Test
    public void testWhenValidatePreferenceWithNegativeMaxInstallmentsNumberReturnFalse() {
        CheckoutPreference preference = stubPreferenceWithNegativeInstallmentsNumbers();
        assertFalse(preference.validInstallmentsPreference());
    }

    ///////////////////EXCEPTIONS tests///////////////////

    @Test
    public void testWhenValidatePreferenceValidNoThrowExceptionReturnTrue() {
        CheckoutPreference preference = stubPreferenceWithOneItemValidActiveAndSomePaymentTypesExcluded();
        Boolean valid = true;

        try {
            preference.validate();
        } catch (CheckoutPreferenceException e) {
            valid = false;
        } finally {
            assertTrue(valid);
        }
    }

    @Test
    public void testWhenValidatePreferenceWithAllPaymentTypesExcludedThrowExceptionReturnTrue() {
        CheckoutPreference preference = stubPreferenceWithOneItemValidActiveButAllPaymentTypesExcluded();

        try {
            preference.validate();
        } catch (CheckoutPreferenceException e) {
            assertTrue(e.getErrorCode() == CheckoutPreferenceException.EXCLUDED_ALL_PAYMENT_TYPES);
        }
    }

    @Test
    public void testWhenValidatePreferenceWithInstallmentsDefaultNumberAndInstallmentsNumberNegativeThrowExceptionReturnTrue() {
        CheckoutPreference preference =
            stubPreferenceWithOneItemValidButInstallmentsDefaultNumberAndInstallmentsNumberNegative();

        try {
            preference.validate();
        } catch (CheckoutPreferenceException e) {
            assertTrue(e.getErrorCode() == CheckoutPreferenceException.INVALID_INSTALLMENTS);
        }
    }

    @Test
    public void testWhenValidatePreferenceWithPreferenceExpiredThrowExceptionReturnTrue() {
        CheckoutPreference preference = stubExpiredPreference();

        try {
            preference.validate();
        } catch (CheckoutPreferenceException e) {
            assertTrue(e.getErrorCode() == CheckoutPreferenceException.EXPIRED_PREFERENCE);
        }
    }

    @Test
    public void testWhenPreferenceIsActiveReturnTrue() {
        CheckoutPreference preference = stubActivePreferenceAndPayer();
        assertTrue(preference.isActive());
    }

    @Test
    public void testWhenPreferenceIsNotActiveReturnFalse() {
        CheckoutPreference preference = stubInactivePreferenceAndPayer();
        assertFalse(preference.isActive());
    }

    @Test
    public void testWhenPreferenceIsNotExpiredReturnFalse() {
        CheckoutPreference preference = stubActivePreferenceAndPayer();
        assertFalse(preference.isExpired());
    }

    @Test
    public void testWhenPreferenceIsExpiredReturnTrue() {
        CheckoutPreference preference = stubExpiredPreference();
        assertTrue(preference.isExpired());
    }

    @Test
    public void testWhenValidatePreferenceWithNullExpirationDateToReturnFalse() {
        CheckoutPreference preference = stubBuilderOneItemAndPayer().setExpirationDate(null).build();
        assertFalse(preference.isExpired());
    }
}