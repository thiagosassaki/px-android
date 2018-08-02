package com.mercadopago.android.px.paymentresult.props;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.plugins.model.BusinessPayment;
import com.mercadopago.android.px.services.util.TextUtil;

public class HeaderProps {

    public static final String HEADER_MODE_WRAP = "wrap";
    public static final String HEADER_MODE_STRETCH = "stretch";

    public final String height;
    public final int background;
    public final int statusBarColor;
    public final int iconImage;
    public final int badgeImage;
    public final String iconUrl;
    public final CharSequence title;
    public final String label;

    private HeaderProps(@NonNull final Builder builder) {
        height = builder.height;
        background = builder.background;
        statusBarColor = builder.statusBarColor;
        iconImage = builder.iconImage;
        iconUrl = builder.iconUrl;
        badgeImage = builder.badgeImage;
        title = builder.title;
        label = builder.label;
    }

    public static HeaderProps from(@NonNull final BusinessPayment businessPayment, @NonNull final Context context) {
        final BusinessPayment.Decorator decorator = businessPayment.getDecorator();
        final Builder builder = new Builder();

        if (businessPayment.getIcon() != 0) {
            builder.setIconImage(businessPayment.getIcon());
        }

        String subtitle;
        if (TextUtil.isEmpty(businessPayment.getSubtitle())) {
            subtitle = decorator.message == 0 ? null : context.getString(decorator.message);
        } else {
            subtitle = businessPayment.getSubtitle();
        }

        builder.setIconUrl(businessPayment.getImageUrl());

        return builder
            .setHeight(HEADER_MODE_WRAP)
            .setBackground(decorator.resColor)
            .setStatusBarColor(decorator.resColor)

            .setIconImage(businessPayment.getIcon())
            .setBadgeImage(decorator.badge)
            .setTitle(businessPayment.getTitle())
            .setLabel(subtitle)
            .build();
    }

    public Builder toBuilder() {
        return new Builder()
            .setHeight(height)
            .setBackground(background)
            .setStatusBarColor(statusBarColor)
            .setIconImage(iconImage)
            .setIconUrl(iconUrl)
            .setBadgeImage(badgeImage)
            .setTitle(title)
            .setLabel(label);
    }

    public static class Builder {
        //TODO definir los valores default
        public String height;
        public int background;
        public int statusBarColor;
        public int iconImage;
        public int badgeImage;
        public String iconUrl;
        public CharSequence title;
        public String label;

        public Builder setBackground(@DrawableRes final int background) {
            this.background = background;
            return this;
        }

        public Builder setStatusBarColor(int statusBarColor) {
            this.statusBarColor = statusBarColor;
            return this;
        }

        public Builder setIconImage(@DrawableRes final int iconImage) {
            this.iconImage = iconImage;
            return this;
        }

        public Builder setIconUrl(final String iconUrl) {
            this.iconUrl = iconUrl;
            return this;
        }

        public Builder setBadgeImage(@DrawableRes final int badgeImage) {
            this.badgeImage = badgeImage;
            return this;
        }

        public Builder setHeight(@NonNull final String height) {
            this.height = height;
            return this;
        }

        public Builder setTitle(@NonNull final CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder setLabel(@Nullable final String label) {
            this.label = label;
            return this;
        }

        public HeaderProps build() {
            return new HeaderProps(this);
        }
    }
}
