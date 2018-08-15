package com.mercadopago.android.px.internal.util;

import android.content.Context;
import android.support.annotation.DrawableRes;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.core.PaymentMethodPlugin;
import com.mercadopago.android.px.internal.di.Session;

public class ResourceUtil {

    private static final String SDK_PREFIX = "px_";
    private static final String DEF_TYPE_DRAWABLE = "drawable";
    public static final String BANK_SUFFIX = "bank";
    public static final String TINT_PREFIX = "grey_";

    @DrawableRes
    private static int getPaymentMethodIcon(final Context context, String id) {
        int resource;
        id = SDK_PREFIX + id;
        try {
            resource = context.getResources().getIdentifier(id, DEF_TYPE_DRAWABLE, context.getPackageName());
        } catch (final Exception e) {
            try {
                resource = context.getResources()
                    .getIdentifier(SDK_PREFIX + BANK_SUFFIX, DEF_TYPE_DRAWABLE, context.getPackageName());
            } catch (final Exception ex) {
                return R.drawable.px_none;
            }
        }
        return resource;
    }

    @DrawableRes
    public static int getIconResource(final Context context, final String id) {
        try {
            final PaymentMethodPlugin paymentMethodPlugin =
                Session.getSession(context).getPluginRepository().getPlugin(id);
            return paymentMethodPlugin.getPaymentMethodInfo(context).icon;
        } catch (final Exception e) {
            return getPaymentMethodIcon(context, id);
        }
    }
}
