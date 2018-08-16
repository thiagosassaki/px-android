package com.mercadopago.android.px.internal.util;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;
import android.support.annotation.NonNull;

public final class LocaleUtil {

    private LocaleUtil() {
    }

    public static String getLanguage(@NonNull final Context context) {
        final Configuration configuration = context.getResources().getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            final LocaleList locales = configuration.getLocales();
            if (!locales.isEmpty()) {
                return locales.get(0).getLanguage();
            }
        }
        return configuration.locale.getLanguage();
    }
}
