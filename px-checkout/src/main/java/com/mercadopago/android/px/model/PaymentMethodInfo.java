package com.mercadopago.android.px.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.features.uicontrollers.paymentmethodsearch.PaymentMethodInfoModel;
import java.io.Serializable;

public class PaymentMethodInfo implements PaymentMethodInfoModel, Serializable {
    private final String id;
    private final String name;
    private final String description;
    @DrawableRes public final
    int icon;

    public PaymentMethodInfo(@NonNull final String id,
        @NonNull final String name,
        @DrawableRes final int icon,
        @NonNull final String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    public PaymentMethodInfo(@NonNull final String id,
        @NonNull final String name,
        @DrawableRes final int icon) {
        this.id = id;
        this.name = name;
        description = null;
        this.icon = icon;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getIcon() {
        return icon;
    }
}