package com.mercadopago.android.px.core;

import android.os.Build;
import android.support.test.InstrumentationRegistry;
import com.mercadopago.android.px.internal.datasource.MercadoPagoServicesAdapter;
import com.mercadopago.android.px.internal.features.CheckoutActivity;
import com.mercadopago.android.px.test.BaseTest;
import junit.framework.Assert;

public class MercadoPagoServicesAdapterTest extends BaseTest<CheckoutActivity> {

    public MercadoPagoServicesAdapterTest() {
        super(CheckoutActivity.class);
    }

    public void testWhenAndroidVersionIsPriorICSThenPropertyKeepAliveFalseElseDefault() {
        new MercadoPagoServicesAdapter(InstrumentationRegistry.getContext(), "DUMMY_PK");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Assert.assertEquals("false", System.getProperty("http.keepAlive"));
        } else {
            Assert.assertEquals(null, System.getProperty("http.keepAlive"));
        }
    }
}
