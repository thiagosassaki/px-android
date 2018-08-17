package com.mercadopago.android.px.testcheckout.idleresources;

import android.support.test.InstrumentationRegistry;
import com.mercadopago.android.px.internal.util.HttpClientUtil;
import com.mercadopago.android.testlib.HttpResource;
import okhttp3.OkHttpClient;

public class CheckoutResource extends HttpResource {
    @Override
    protected OkHttpClient getClient() {
        return HttpClientUtil.getClient(InstrumentationRegistry.getContext(), 10, 10, 10);
    }
}
