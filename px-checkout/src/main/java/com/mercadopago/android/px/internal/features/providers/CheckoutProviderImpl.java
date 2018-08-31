package com.mercadopago.android.px.internal.features.providers;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.provider.FontRequest;
import android.support.v4.provider.FontsContractCompat;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.internal.datasource.MercadoPagoESC;
import com.mercadopago.android.px.internal.datasource.MercadoPagoServicesAdapter;
import com.mercadopago.android.px.internal.features.uicontrollers.FontCache;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.internal.util.QueryBuilder;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.CheckoutPreferenceException;
import com.mercadopago.android.px.model.exceptions.ExceptionHandler;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.services.Callback;

public class CheckoutProviderImpl implements CheckoutProvider {

    private final Context context;
    private final MercadoPagoServicesAdapter mercadoPagoServicesAdapter;
    private final MercadoPagoESC mercadoPagoESC;
    private Handler mHandler;

    public CheckoutProviderImpl(@NonNull final Context context,
        @NonNull String publicKey,
        @NonNull String privateKey,
        @NonNull final MercadoPagoESC mercadoPagoESC) {
        this.context = context;
        mercadoPagoServicesAdapter = new MercadoPagoServicesAdapter(context, publicKey, privateKey);
        this.mercadoPagoESC = mercadoPagoESC;
    }

    @Override
    public void fetchFonts() {
        if (!FontCache.hasTypeface(FontCache.CUSTOM_REGULAR_FONT)) {
            fetchRegularFont();
        }
        if (!FontCache.hasTypeface(FontCache.CUSTOM_MONO_FONT)) {
            fetchMonoFont();
        }
        if (!FontCache.hasTypeface(FontCache.CUSTOM_LIGHT_FONT)) {
            fetchLightFont();
        }
    }

    private void fetchRegularFont() {
        FontsContractCompat.FontRequestCallback regularFontCallback = new FontsContractCompat
            .FontRequestCallback() {
            @Override
            public void onTypefaceRetrieved(Typeface typeface) {
                FontCache.setTypeface(FontCache.CUSTOM_REGULAR_FONT, typeface);
            }

            @Override
            public void onTypefaceRequestFailed(int reason) {
                //Do nothing
            }
        };
        FontsContractCompat.requestFont(context,
            getFontRequest(FontCache.FONT_ROBOTO, QueryBuilder.WIDTH_DEFAULT,
                QueryBuilder.WEIGHT_DEFAULT, QueryBuilder.ITALIC_DEFAULT),
            regularFontCallback,
            getHandlerThreadHandler());
    }

    private void fetchLightFont() {
        FontsContractCompat.FontRequestCallback lightFontCallback = new FontsContractCompat
            .FontRequestCallback() {
            @Override
            public void onTypefaceRetrieved(Typeface typeface) {
                FontCache.setTypeface(FontCache.CUSTOM_LIGHT_FONT, typeface);
            }

            @Override
            public void onTypefaceRequestFailed(int reason) {
                //Do nothing
            }
        };
        FontsContractCompat.requestFont(context,
            getFontRequest(FontCache.FONT_ROBOTO, QueryBuilder.WIDTH_DEFAULT,
                QueryBuilder.WEIGHT_LIGHT, QueryBuilder.ITALIC_DEFAULT),
            lightFontCallback,
            getHandlerThreadHandler());
    }

    private void fetchMonoFont() {
        FontsContractCompat.FontRequestCallback monoFontCallback = new FontsContractCompat
            .FontRequestCallback() {
            @Override
            public void onTypefaceRetrieved(Typeface typeface) {
                FontCache.setTypeface(FontCache.CUSTOM_MONO_FONT, typeface);
            }

            @Override
            public void onTypefaceRequestFailed(int reason) {
                //Do nothing
            }
        };
        FontsContractCompat.requestFont(context,
            getFontRequest(FontCache.FONT_ROBOTO_MONO, QueryBuilder.WIDTH_DEFAULT,
                QueryBuilder.WEIGHT_DEFAULT, QueryBuilder.ITALIC_DEFAULT),
            monoFontCallback,
            getHandlerThreadHandler());
    }

    private FontRequest getFontRequest(String fontName, int width, int weight, float italic) {
        QueryBuilder queryBuilder = new QueryBuilder(fontName)
            .withWidth(width)
            .withWeight(weight)
            .withItalic(italic)
            .withBestEffort(true);
        String query = queryBuilder.build();

        return new FontRequest(
            "com.google.android.gms.fonts",
            "com.google.android.gms",
            query,
            R.array.com_google_android_gms_fonts_certs);
    }

    private Handler getHandlerThreadHandler() {
        if (mHandler == null) {
            HandlerThread handlerThread = new HandlerThread("fonts");
            handlerThread.start();
            mHandler = new Handler(handlerThread.getLooper());
        }
        return mHandler;
    }

    @Override
    public void getCheckoutPreference(String checkoutPreferenceId,
        final TaggedCallback<CheckoutPreference> taggedCallback) {
        mercadoPagoServicesAdapter.getCheckoutPreference(checkoutPreferenceId, new Callback<CheckoutPreference>() {
            @Override
            public void success(CheckoutPreference checkoutPreference) {
                taggedCallback.onSuccess(checkoutPreference);
            }

            @Override
            public void failure(ApiException apiException) {
                taggedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.GET_PREFERENCE));
            }
        });
    }

    @Override
    public String getCheckoutExceptionMessage(CheckoutPreferenceException exception) {
        return ExceptionHandler.getErrorMessage(context, exception);
    }

    @Override
    public String getCheckoutExceptionMessage(final Exception exception) {
        return context.getString(R.string.px_standard_error_message);
    }
}