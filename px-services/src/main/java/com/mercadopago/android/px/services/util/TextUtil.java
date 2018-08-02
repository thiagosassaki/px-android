package com.mercadopago.android.px.services.util;

import android.support.annotation.Nullable;

/**
 * Created by mreverter on 1/31/17.
 */

public final class TextUtil {
    private TextUtil() {
    }

    public static boolean isEmpty(@Nullable final String text) {
        return text == null || text.isEmpty();
    }
}
