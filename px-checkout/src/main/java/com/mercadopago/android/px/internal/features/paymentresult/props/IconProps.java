package com.mercadopago.android.px.internal.features.paymentresult.props;

import android.support.annotation.NonNull;

public class IconProps {

    public final int iconImage;
    public final String iconUrl;
    public final int badgeImage;

    public IconProps(final int iconImage, final String iconUrl, final int badgeImage) {
        this.iconImage = iconImage;
        this.iconUrl = iconUrl;
        this.badgeImage = badgeImage;
    }

    public IconProps(@NonNull final Builder builder) {
        iconImage = builder.iconImage;
        iconUrl = builder.iconUrl;
        badgeImage = builder.badgeImage;
    }

    public Builder toBuilder() {
        return new Builder()
            .setIconImage(iconImage)
            .setIconUrl(iconUrl)
            .setBadgeImage(badgeImage);
    }

    public static class Builder {

        public int iconImage ;
        public String iconUrl;
        public int badgeImage;

        public Builder setIconImage(final int iconImage) {
            this.iconImage = iconImage;
            return this;
        }

        public Builder setIconUrl(final String iconUrl) {
            this.iconUrl = iconUrl;
            return this;
        }

        public Builder setBadgeImage(final int badgeImage) {
            this.badgeImage = badgeImage;
            return this;
        }

        public IconProps build() {
            return new IconProps(this);
        }
    }
}
