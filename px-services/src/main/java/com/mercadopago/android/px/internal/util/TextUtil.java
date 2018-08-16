package com.mercadopago.android.px.internal.util;

import android.support.annotation.Nullable;

public final class TextUtil {

    private TextUtil() {
        //Do nothing
    }

    public static boolean isEmpty(@Nullable final CharSequence text) {
        return text == null || text.length() <= 0;
    }

    public static boolean isNotEmpty(@Nullable final CharSequence text) {
        return !isEmpty(text);
    }
}
