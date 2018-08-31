package com.mercadopago.android.px.internal.util;

import android.content.Context;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.model.PaymentMethod;

/**
 * Created by marlanti on 7/14/17.
 */

public class MPCardUIUtils {

    public static final int NEUTRAL_CARD_COLOR = R.color.px_white;
    public static final int FULL_TEXT_VIEW_COLOR = R.color.px_base_text_alpha;
    public static final String NEUTRAL_CARD_COLOR_NAME = "px_white";
    public static final String FULL_TEXT_VIEW_COLOR_NAME = "px_base_text_alpha";

    public static int getCardColor(PaymentMethod paymentMethod, Context context) {
        String colorName = "px_" + paymentMethod.getId().toLowerCase();
        int color = context.getResources().getIdentifier(colorName, "color", context.getPackageName());
        if (color == 0) {
            color = context.getResources().getIdentifier(NEUTRAL_CARD_COLOR_NAME, "color", context.getPackageName());
        }
        return color;
    }

    public static int getCardFontColor(PaymentMethod paymentMethod, Context context) {
        if (paymentMethod == null) {
            return FULL_TEXT_VIEW_COLOR;
        }
        String colorName = "px_font_" + paymentMethod.getId().toLowerCase();
        int color = context.getResources().getIdentifier(colorName, "color", context.getPackageName());
        if (color == 0) {
            color = context.getResources().getIdentifier(FULL_TEXT_VIEW_COLOR_NAME, "color", context.getPackageName());
        }
        return color;
    }
}
