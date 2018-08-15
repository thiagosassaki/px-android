package com.mercadopago.android.px.model;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.mercadopago.android.px.internal.features.CheckoutActivity;
import com.mercadopago.android.px.test.StaticMock;
import java.util.ArrayList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SettingTest {

    @Rule
    public ActivityTestRule<CheckoutActivity> mTestRule = new ActivityTestRule<>(CheckoutActivity.class, false, false);

    @Test
    public void whenValidBinThenGetSetting() {
        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());
        Setting setting = Setting.getSettingByBin(paymentMethod.getSettings(), "466057");
        assertTrue(setting != null);
    }

    @Test
    public void whenInvalidBinThenReturnNullSetting() {
        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());
        Setting setting = Setting.getSettingByBin(paymentMethod.getSettings(), "888888");
        assertTrue(setting == null);
    }

    @Test
    public void whenNoSettingsThenReturnNullSetting() {
        Setting setting = Setting.getSettingByBin(new ArrayList<Setting>(), "466057");
        assertTrue(setting == null);
        setting = Setting.getSettingByBin(null, "466057");
        assertTrue(setting == null);
    }
}

