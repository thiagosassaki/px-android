package com.mercadopago.android.px.util;

import android.app.Activity;
import android.content.Intent;
import com.mercadopago.android.px.ErrorActivity;
import com.mercadopago.android.px.exceptions.MercadoPagoError;
import com.mercadopago.android.px.internal.di.Session;

/**
 * Created by mreverter on 9/5/16.
 */

public final class ErrorUtil {

    public static final int ERROR_REQUEST_CODE = 94;
    public static final String ERROR_EXTRA_KEY = "mercadoPagoError";
    public static final String PUBLIC_KEY_EXTRA = "publicKey";

    private ErrorUtil() {
    }

    public static void startErrorActivity(final Activity launcherActivity, final String message,
        final boolean recoverable) {
        final MercadoPagoError mercadoPagoError = new MercadoPagoError(message, recoverable);
        startErrorActivity(launcherActivity, mercadoPagoError);
    }

    public static void startErrorActivity(final Activity launcherActivity, final String message,
        final String errorDetail,
        final boolean recoverable) {
        final MercadoPagoError mercadoPagoError = new MercadoPagoError(message, errorDetail, recoverable);
        startErrorActivity(launcherActivity, mercadoPagoError);
    }

    public static void startErrorActivity(final Activity launcherActivity, final MercadoPagoError mercadoPagoError) {
        final String publicKey =
            Session.getSession(launcherActivity).getConfigurationModule().getPaymentSettings().getPublicKey();

        final Intent intent = new Intent(launcherActivity, ErrorActivity.class);
        intent.putExtra(ERROR_EXTRA_KEY, JsonUtil.getInstance().toJson(mercadoPagoError));
        intent.putExtra(PUBLIC_KEY_EXTRA, publicKey);
        launcherActivity.startActivityForResult(intent, ERROR_REQUEST_CODE);
    }
}
