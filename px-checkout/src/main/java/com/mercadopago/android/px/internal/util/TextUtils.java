package com.mercadopago.android.px.internal.util;

import android.support.annotation.Nullable;

public final class TextUtils {

    private TextUtils() {
    }

    public static boolean isEmpty(@Nullable final String text) {
        return text == null || text.isEmpty();
    }

    public static boolean isEmpty(CharSequence text) {
        return text == null || text.length() <= 0;
    }

    public static boolean isNotEmpty(@Nullable final String text) {
        return !isEmpty(text);
    }
}
