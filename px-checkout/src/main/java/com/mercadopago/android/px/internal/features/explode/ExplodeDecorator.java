package com.mercadopago.android.px.internal.features.explode;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

public final class ExplodeDecorator {

    private final int primaryColor;
    private final int darkPrimaryColor;
    private final int statusIcon;

    public ExplodeDecorator(@ColorRes final int primaryColor,
        @ColorRes final int darkPrimaryColor,
        @DrawableRes final int statusIcon) {
        this.primaryColor = primaryColor;
        this.darkPrimaryColor = darkPrimaryColor;
        this.statusIcon = statusIcon;
    }

    @ColorRes
    public int getPrimaryColor() {
        return primaryColor;
    }

    @ColorRes
    public int getDarkPrimaryColor() {
        return darkPrimaryColor;
    }

    @DrawableRes
    public int getStatusIcon() {
        return statusIcon;
    }
}