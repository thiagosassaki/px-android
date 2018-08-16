package com.mercadopago.android.px.internal.util;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.internal.features.ErrorActivity;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;

import static com.mercadopago.android.px.core.MercadoPagoCheckout.EXTRA_ERROR;

/**
 * Created by mreverter on 9/5/16.
 */

public final class ErrorUtil {

    public static final int ERROR_REQUEST_CODE = 94;

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
        intent.putExtra(EXTRA_ERROR, JsonUtil.getInstance().toJson(mercadoPagoError));
        intent.putExtra(PUBLIC_KEY_EXTRA, publicKey);
        launcherActivity.startActivityForResult(intent, ERROR_REQUEST_CODE);
    }

    public static void showApiExceptionError(@NonNull final Activity activity,
        final ApiException apiException,
        final String requestOrigin) {

        MercadoPagoError mercadoPagoError;
        String errorMessage;

        if (!ApiUtil.checkConnection(activity)) {
            errorMessage = activity.getString(R.string.px_no_connection_message);
            mercadoPagoError = new MercadoPagoError(errorMessage, true);
        } else {
            mercadoPagoError = new MercadoPagoError(apiException, requestOrigin);
        }
        ErrorUtil.startErrorActivity(activity, mercadoPagoError);
    }

}
