package com.mercadopago.android.px.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.mercadopago.android.px.internal.util.ParcelableUtil;
import com.mercadopago.android.px.internal.util.TextUtil;

@SuppressWarnings("unused")
public class BusinessPayment implements IPayment, Parcelable {

    @NonNull private final String title;
    @NonNull private final Decorator decorator;
    @NonNull private final String paymentStatus;
    @NonNull private final String paymentStatusDetail;
    private final boolean shouldShowPaymentMethod;
    private final int iconId;

    @Nullable private final String help;
    @Nullable private final ExitAction exitActionPrimary;
    @Nullable private final ExitAction exitActionSecondary;
    @Nullable private final String statementDescription;
    @Nullable private final String receiptId;
    @Nullable private final String imageUrl;
    @Nullable private final ExternalFragment topFragment;
    @Nullable private final ExternalFragment bottomFragment;


    @Nullable
    private final String subtitle;

    /* default */ BusinessPayment(final Builder builder) {
        help = builder.help;
        title = builder.title;
        decorator = builder.decorator;
        iconId = builder.iconId;
        shouldShowPaymentMethod = builder.shouldShowPaymentMethod;
        exitActionPrimary = builder.buttonPrimary;
        exitActionSecondary = builder.buttonSecondary;
        statementDescription = builder.statementDescription;
        receiptId = builder.receiptId;
        imageUrl = builder.imageUrl;
        topFragment = builder.topFragment;
        bottomFragment = builder.bottomFragment;
        paymentStatus = builder.paymentStatus;
        paymentStatusDetail = builder.paymentStatusDetail;
        subtitle = builder.subtitle;
    }

    protected BusinessPayment(final Parcel in) {
        iconId = in.readInt();
        title = in.readString();
        shouldShowPaymentMethod = in.readByte() != 0;
        exitActionPrimary = in.readParcelable(ExitAction.class.getClassLoader());
        exitActionSecondary = in.readParcelable(ExitAction.class.getClassLoader());
        decorator = Decorator.fromName(in.readString());
        help = in.readString();
        statementDescription = in.readString();
        receiptId = in.readString();
        imageUrl = in.readString();
        topFragment = in.readParcelable(ExternalFragment.class.getClassLoader());
        bottomFragment = in.readParcelable(ExternalFragment.class.getClassLoader());
        paymentStatus = in.readString();
        paymentStatusDetail = in.readString();
        subtitle = ParcelableUtil.getOptionalString(in);
    }

    public static final Creator<BusinessPayment> CREATOR = new Creator<BusinessPayment>() {
        @Override
        public BusinessPayment createFromParcel(final Parcel in) {
            return new BusinessPayment(in);
        }

        @Override
        public BusinessPayment[] newArray(final int size) {
            return new BusinessPayment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(iconId);
        dest.writeString(title);
        dest.writeByte((byte) (shouldShowPaymentMethod ? 1 : 0));
        dest.writeParcelable(exitActionPrimary, flags);
        dest.writeParcelable(exitActionSecondary, flags);
        dest.writeString(decorator.name);
        dest.writeString(help);
        dest.writeString(statementDescription);
        dest.writeString(receiptId);
        dest.writeString(imageUrl);
        dest.writeParcelable(topFragment, 0);
        dest.writeParcelable(bottomFragment, 0);
        dest.writeString(paymentStatus);
        dest.writeString(paymentStatusDetail);
        ParcelableUtil.writeOptional(dest, subtitle);
    }

    public boolean hasReceipt() {
        return receiptId != null;
    }

    public boolean hasTopFragment() {
        return getTopFragment() != null;
    }

    public boolean hasBottomFragment() {
        return getBottomFragment() != null;
    }

    @NonNull
    public Decorator getDecorator() {
        return decorator;
    }

    public int getIcon() {
        return iconId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public boolean hasHelp() {
        return TextUtil.isNotEmpty(help);
    }

    @Nullable
    public ExitAction getSecondaryAction() {
        return exitActionSecondary;
    }

    @Nullable
    public ExitAction getPrimaryAction() {
        return exitActionPrimary;
    }

    @Nullable
    public String getHelp() {
        return help;
    }

    public boolean shouldShowPaymentMethod() {
        return shouldShowPaymentMethod;
    }

    @Nullable
    @Override
    public Long getId() {
        return Long.getLong(receiptId);
    }

    @Override
    @Nullable
    public String getStatementDescription() {
        return statementDescription;
    }

    @Nullable
    public String getReceipt() {
        return receiptId;
    }

    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }

    @Nullable
    public ExternalFragment getTopFragment() {
        return topFragment;
    }

    @Nullable
    public ExternalFragment getBottomFragment() {
        return bottomFragment;
    }

    @Override
    @NonNull
    public String getPaymentStatus() {
        return paymentStatus;
    }

    @Override
    @NonNull
    public String getPaymentStatusDetail() {
        return paymentStatusDetail;
    }

    @Nullable
    public String getSubtitle() {
        return subtitle;
    }

    public enum Decorator {
        APPROVED("APPROVED"),
        REJECTED("REJECTED"),
        PENDING("PENDING");

        public final String name;

        Decorator(final String name) {
            this.name = name;
        }

        public static Decorator fromName(final String text) {
            for (final Decorator s : Decorator.values()) {
                if (s.name.equalsIgnoreCase(text)) {
                    return s;
                }
            }
            throw new IllegalStateException("Invalid decorator");
        }
    }

    public static final class Builder {

        // Mandatory values
        /* default */ @NonNull final Decorator decorator;
        /* default */ @DrawableRes final int iconId;
        /* default */ @NonNull final String title;
        /* default */ @NonNull final String paymentStatus;
        /* default */ @NonNull final String paymentStatusDetail;

        // Optional values
        /* default */ @Nullable String imageUrl;
        /* default */ boolean shouldShowPaymentMethod;
        /* default */ @Nullable String statementDescription;
        /* default */ @Nullable ExitAction buttonPrimary;
        /* default */ @Nullable ExitAction buttonSecondary;
        /* default */ @Nullable String help;
        /* default */ @Nullable String receiptId;
        /* default */ @Nullable String subtitle;

        ExternalFragment topFragment;
        ExternalFragment bottomFragment;

        public Builder(@NonNull final Decorator decorator,
            @NonNull final String paymentStatus,
            @NonNull final String paymentStatusDetail,
            @DrawableRes final int iconId,
            @NonNull final String title) {
            this.title = title;
            this.decorator = decorator;
            this.iconId = iconId;
            this.paymentStatus = paymentStatus;
            this.paymentStatusDetail = paymentStatusDetail;
            shouldShowPaymentMethod = false;
            buttonPrimary = null;
            buttonSecondary = null;
            help = null;
            receiptId = null;
            imageUrl = null;
        }

        public Builder(@NonNull final Decorator decorator,
            @NonNull final String paymentStatus,
            @NonNull final String paymentStatusDetail,
            @NonNull final String imageUrl,
            @NonNull final String title) {
            this(decorator, paymentStatus, paymentStatusDetail, 0, title);
            this.imageUrl = imageUrl;
        }

        public BusinessPayment build() {
            if (buttonPrimary == null && buttonSecondary == null) {
                throw new IllegalStateException("At least one button should be provided for BusinessPayment");
            }
            return new BusinessPayment(this);
        }

        /**
         * if Exit action is set, then a big primary button
         * will appear and the click action will trigger a resCode
         * that will be the same of the Exit action added.
         *
         * @param exitAction a {@link ExitAction }
         * @return builder
         */
        public Builder setPrimaryButton(@Nullable final ExitAction exitAction) {
            buttonPrimary = exitAction;
            return this;
        }

        /**
         * if Exit action is set, then a small secondary button
         * will appear and the click action will trigger a resCode
         * that will be the same of the Exit action added.
         *
         * @param exitAction a {@link ExitAction }
         * @return builder
         */
        public Builder setSecondaryButton(@Nullable final ExitAction exitAction) {
            buttonSecondary = exitAction;
            return this;
        }

        /**
         * if help is set, then a small box with help instructions will appear
         *
         * @param help a help message
         * @return builder
         */
        public Builder setHelp(@Nullable final String help) {
            this.help = help;
            return this;
        }

        /**
         * If value true is set, then payment method box
         * will appear with the amount value and payment method
         * options that were selected by the user.
         *
         * @param visible visibility mode
         * @return builder
         */
        public Builder setPaymentMethodVisibility(final boolean visible) {
            shouldShowPaymentMethod = visible;
            return this;
        }

        /**
         * If value true is set on {@link #setPaymentMethodVisibility }
         * and the payment method is credit card
         * then the statementDescription will be shown on payment method view.
         *
         * @param statementDescription disclaimer text
         * @return builder
         */
        public Builder setStatementDescription(final String statementDescription) {
            this.statementDescription = statementDescription;
            return this;
        }

        /**
         * If value is set, then receipt view will appear.
         *
         * @param receiptId the receipt id to be shown.
         * @return builder
         */
        public Builder setReceiptId(final String receiptId) {
            this.receiptId = receiptId;
            return this;
        }

        /**
         * Custom fragment that will appear before payment method description
         * inside Business result screen.
         *
         * @param zClass fragment class
         * @return builder
         */
        public Builder setTopFragment(@NonNull final Class<? extends Fragment> zClass, @Nullable final Bundle args) {
            topFragment = new ExternalFragment(zClass, args);
            return this;
        }

        /**
         * Custom fragment that will appear after payment method description
         * inside Business result screen.
         *
         * @param zClass fragment class
         * @return builder
         */
        public Builder setBottomFragment(@NonNull final Class<? extends Fragment> zClass, @Nullable final Bundle args) {
            bottomFragment = new ExternalFragment(zClass, args);
            return this;
        }

        /**
         * When subtitle is set, then default {@link Decorator} subtitle will be replaced
         * on the screen with it.
         *
         * @param subtitle subtitle text
         * @return builder
         */
        public Builder setSubtitle(@Nullable final String subtitle) {
            this.subtitle = subtitle;
            return this;
        }
    }
}