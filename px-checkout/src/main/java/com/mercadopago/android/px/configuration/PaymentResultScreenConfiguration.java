package com.mercadopago.android.px.configuration;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.mercadopago.android.px.internal.features.paymentresult.model.Badge;
import com.mercadopago.android.px.model.ExternalFragment;
import com.mercadopago.android.px.model.Payment;
import java.io.Serializable;

/**
 * This object declares custom preferences for "Congrats" screen
 */
public final class PaymentResultScreenConfiguration implements Serializable {

    //    private final Integer titleBackgroundColor;
//    private final String approvedTitle;
//    private final String approvedSubtitle;
//    private final Integer approvedIcon;
//    private final String approvedUrlIcon;
//    private final String approvedLabelText;
//    @Badge.ApprovedBadges private final String approvedBadge;
//    private final String pendingTitle;
//    private final String pendingSubtitle;
//    private final String pendingContentTitle;
//    private final String pendingContentText;
//    private final Integer pendingIcon;
//    private final String pendingUrlIcon;
//    private final String exitButtonTitle;
//    private final String secondaryPendingExitButtonTitle;
//    private final String secondaryCongratsExitButtonTitle;
//    private final String secondaryRejectedExitButtonTitle;
//    private final String rejectedTitle;
//    private final String rejectedSubtitle;
//    private final Integer rejectedIcon;
//    private final String rejectedUrlIcon;
//    private final String rejectedIconSubtext;
//    private String rejectedContentTitle;
//    private final String rejectedContentText;
//    private boolean enableCongratsSecondaryExitButton = true;
//    private boolean enablePendingSecondaryExitButton = true;
//    private boolean enableRejectedSecondaryExitButton = true;
//    private boolean enablePendingContentText = true;
//    private boolean enablePendingContentTitle = true;
//    private boolean enableRejectedContentText = true;
//    private boolean enableRejectedContentTitle = true;
//    private boolean enableRejectedIconSubtext = true;
//    private boolean enableApprovedReceipt = true;
//    private boolean enableApprovedAmount = true;
//    private boolean enableApprovedPaymentMethodInfo = true;
//    private boolean enableRejectedLabelText = true;
//
//    private final Integer secondaryRejectedExitResultCode;
//    private final Integer secondaryCongratsExitResultCode;
//    private final Integer secondaryPendingExitResultCode;
//    private final Boolean rejectionRetryEnabled;
    @Nullable private final ExternalFragment topFragment;
    @Nullable private final ExternalFragment bottomFragment;

    /* default */ PaymentResultScreenConfiguration(@NonNull final Builder builder) {
//        titleBackgroundColor = builder.titleBackgroundColor;
//        approvedTitle = builder.approvedTitle;
//        approvedSubtitle = builder.approvedSubtitle;
//        approvedIcon = builder.approvedIcon;
//        approvedUrlIcon = builder.approvedUrlIcon;
//        approvedLabelText = builder.approvedLabelText;
//        approvedBadge = builder.approvedBadge;
//        pendingTitle = builder.pendingTitle;
//        pendingSubtitle = builder.pendingSubtitle;
//        pendingContentTitle = builder.pendingContentTitle;
//        pendingContentText = builder.pendingContentText;
//        pendingIcon = builder.pendingIcon;
//        pendingUrlIcon = builder.pendingUrlIcon;
//        exitButtonTitle = builder.exitButtonTitle;
//        secondaryPendingExitButtonTitle = builder.secondaryPendingExitButtonTitle;
//        secondaryPendingExitResultCode = builder.secondaryPendingExitResultCode;
//        secondaryCongratsExitButtonTitle = builder.secondaryCongratsExitButtonTitle;
//        secondaryCongratsExitResultCode = builder.secondaryCongratsExitResultCode;
//        secondaryRejectedExitButtonTitle = builder.secondaryRejectedExitButtonTitle;
//        secondaryRejectedExitResultCode = builder.secondaryRejectedExitResultCode;
//        rejectedTitle = builder.rejectedTitle;
//        rejectedSubtitle = builder.rejectedSubtitle;
//        rejectedIcon = builder.rejectedIcon;
//        rejectedUrlIcon = builder.rejectedUrlIcon;
//        rejectedIconSubtext = builder.rejectedIconSubtext;
//        rejectedContentTitle = builder.rejectedContentTitle;
//        rejectedContentText = builder.rejectedContentText;
//        rejectedContentTitle = builder.rejectedContentTitle;
//        rejectionRetryEnabled = builder.rejectionRetryEnabled;
//
//        enableCongratsSecondaryExitButton = builder.enableCongratsSecondaryExitButton;
//        enablePendingSecondaryExitButton = builder.enablePendingSecondaryExitButton;
//        enableRejectedSecondaryExitButton = builder.enableRejectedSecondaryExitButton;
//        enablePendingContentText = builder.enablePendingContentText;
//        enablePendingContentTitle = builder.enablePendingContentTitle;
//        enableRejectedContentText = builder.enableRejectedContentText;
//        enableRejectedContentTitle = builder.enableRejectedContentTitle;
//        enableRejectedIconSubtext = builder.enableRejectedIconSubtext;
//        enableApprovedReceipt = builder.enableApprovedReceipt;
//        enableApprovedAmount = builder.enableApprovedAmount;
//        enableApprovedPaymentMethodInfo = builder.enableApprovedPaymentMethodInfo;
//        enableRejectedLabelText = builder.enableRejectedLabelText;
        topFragment = builder.topFragment;
        bottomFragment = builder.bottomFragment;
    }

    public boolean hasTopFragment() {
        return getTopFragment() != null;
    }

    public boolean hasBottomFragment() {
        return getBottomFragment() != null;
    }

    @Nullable
    public ExternalFragment getTopFragment() {
        return topFragment;
    }

    @Nullable
    public ExternalFragment getBottomFragment() {
        return bottomFragment;
    }

    @Deprecated
    public boolean hasCustomizedImageIcon(final String status, final String statusDetail) {
        if (Payment.StatusCodes.STATUS_APPROVED.equals(status)) {
            return getApprovedIcon() != null;
        } else if (Payment.isPendingStatus(status, statusDetail)) {
            return getPendingIcon() != null;
        } else if (Payment.StatusCodes.STATUS_REJECTED.equals(status)) {
            return getRejectedIcon() != null;
        }
        return false;
    }

    @Deprecated
    public String getPreferenceUrlIcon(final String status, final String statusDetail) {
        if (Payment.StatusCodes.STATUS_APPROVED.equals(status)) {
            return getApprovedUrlIcon();
        } else if (Payment.isPendingStatus(status, statusDetail)) {
            return getPendingUrlIcon();
        } else if (Payment.StatusCodes.STATUS_REJECTED.equals(status)) {
            return getRejectedUrlIcon();
        }
        return null;
    }

    @Deprecated
    public int getPreferenceIcon(final String status, final String statusDetail) {
        if (Payment.StatusCodes.STATUS_APPROVED.equals(status)) {
            return getApprovedIcon();
        } else if (Payment.isPendingStatus(status, statusDetail)) {
            return getPendingIcon();
        } else if (Payment.StatusCodes.STATUS_REJECTED.equals(status)) {
            return getRejectedIcon();
        }
        return 0;
    }

    @Deprecated
    public String getApprovedTitle() {
//        return approvedTitle;
        return null;
    }

    @Deprecated
    public String getApprovedSubtitle() {
//        return approvedSubtitle;
        return null;
    }

    @Deprecated
    public Integer getApprovedIcon() {
//        return approvedIcon;
        return null;
    }

    @Deprecated
    public String getApprovedUrlIcon() {
//        return approvedUrlIcon;
        return null;
    }

    @Deprecated
    public String getApprovedLabelText() {
//        return approvedLabelText;
        return null;
    }

    @Deprecated
    @Badge.ApprovedBadges
    public String getApprovedBadge() {
//        return approvedBadge;
        return null;
    }

    @Deprecated
    public String getPendingTitle() {
//        return pendingTitle;
        return null;
    }

    @Deprecated
    public String getPendingSubtitle() {
//        return pendingSubtitle;
        return null;
    }

    @Deprecated
    public String getExitButtonTitle() {
//        return exitButtonTitle;
        return null;
    }

    @Deprecated
    public String getPendingContentTitle() {
//        return pendingContentTitle;
        return null;
    }

    @Deprecated
    public String getPendingContentText() {
//        return pendingContentText;
        return null;
    }

    @Deprecated
    public String getSecondaryPendingExitButtonTitle() {
//        return secondaryPendingExitButtonTitle;
        return null;
    }

    @Deprecated
    public String getSecondaryCongratsExitButtonTitle() {
//        return secondaryCongratsExitButtonTitle;
        return null;
    }

    @Deprecated
    public Integer getSecondaryCongratsExitResultCode() {
//        return secondaryCongratsExitResultCode;
        return null;
    }

    @Deprecated
    public String getSecondaryRejectedExitButtonTitle() {
//        return secondaryRejectedExitButtonTitle;
        return null;
    }

    @Deprecated
    public String getRejectedTitle() {
//        return rejectedTitle;
        return null;
    }

    @Deprecated
    public String getRejectedSubtitle() {
//        return rejectedSubtitle;
        return null;
    }

    @Deprecated
    public String getRejectedContentTitle() {
//        return rejectedContentTitle;
        return null;
    }

    @Deprecated
    public String getRejectedContentText() {
//        return rejectedContentText;
        return null;
    }

    @Deprecated
    public Integer getRejectedIcon() {
//        return rejectedIcon;
        return null;
    }

    @Deprecated
    public String getRejectedUrlIcon() {
//        return rejectedUrlIcon;
        return null;
    }

    @Deprecated
    public String getRejectedIconSubtext() {
//        return rejectedIconSubtext;
        return null;
    }

    @Deprecated
    public Integer getPendingIcon() {
//        return pendingIcon;
        return null;
    }

    @Deprecated
    public String getPendingUrlIcon() {
//        return pendingUrlIcon;
        return null;
    }

    @Deprecated
    public boolean isApprovedReceiptEnabled() {
//        return enableApprovedReceipt;
        return true;
    }

    @Deprecated
    public boolean isApprovedAmountEnabled() {
//        return enableApprovedAmount;
        return true;
    }

    @Deprecated
    public boolean isApprovedPaymentMethodInfoEnabled() {
//        return enableApprovedPaymentMethodInfo;
        return true;
    }

    @Deprecated
    public boolean isCongratsSecondaryExitButtonEnabled() {
//        return enableCongratsSecondaryExitButton;
        return true;
    }

    @Deprecated
    public boolean isPendingSecondaryExitButtonEnabled() {
//        return enablePendingSecondaryExitButton;
        return true;
    }

    @Deprecated
    public boolean isRejectedSecondaryExitButtonEnabled() {
//        return enableRejectedSecondaryExitButton;
        return true;
    }

    @Deprecated
    public boolean isPendingContentTextEnabled() {
//        return enablePendingContentText;
        return true;
    }

    @Deprecated
    public boolean isPendingContentTitleEnabled() {
//        return enablePendingContentTitle;
        return true;
    }

    @Deprecated
    public boolean isRejectedContentTextEnabled() {
//        return enableRejectedContentText;
        return true;
    }

    @Deprecated
    public boolean isRejectedContentTitleEnabled() {
//        return enableRejectedContentTitle;
        return true;
    }

    @Deprecated
    public boolean isRejectedIconSubtextEnabled() {
//        return enableRejectedIconSubtext;
        return true;
    }

    @Deprecated
    public boolean isRejectedLabelTextEnabled() {
//        return enableRejectedLabelText;
        return true;
    }

    @Deprecated
    public Integer getSecondaryRejectedExitResultCode() {
//        return secondaryRejectedExitResultCode;
        return null;
    }

    @Deprecated
    public Integer getSecondaryPendingExitResultCode() {
//        return secondaryPendingExitResultCode;
        return null;
    }

    @Deprecated
    public boolean hasTitleBackgroundColor() {
//        return titleBackgroundColor != null;
        return false;
    }

    @Deprecated
    public Integer getTitleBackgroundColor() {
//        return titleBackgroundColor;
        return null;
    }

    @Deprecated
    public boolean isRejectionRetryEnabled() {
//        return rejectionRetryEnabled;
        return true;
    }

    public static class Builder {

        /* default */ @Nullable ExternalFragment topFragment;
        /* default */ @Nullable ExternalFragment bottomFragment;

//        private String approvedTitle;
//        private String approvedLabelText;
//        @Badge.ApprovedBadges private
//        String approvedBadge;
//
//        private Integer titleBackgroundColor;
//        private String approvedSubtitle;
//        private Integer approvedIcon;
//        private String approvedUrlIcon;
//        private String pendingTitle;
//        private String pendingSubtitle;
//        private String pendingContentTitle;
//        private String pendingContentText;
//        private Integer pendingIcon;
//        private String pendingUrlIcon;
//        private String exitButtonTitle;
//        private String secondaryPendingExitButtonTitle;
//        private String secondaryCongratsExitButtonTitle;
//        private String secondaryRejectedExitButtonTitle;
//        private String rejectedTitle;
//        private String rejectedSubtitle;
//        private Integer rejectedIcon;
//        private String rejectedUrlIcon;
//        private String rejectedIconSubtext;
//        private String rejectedContentTitle;
//        private String rejectedContentText;
//        private boolean rejectionRetryEnabled = true;
//        private boolean enablePendingContentText = true;
//        private boolean enablePendingContentTitle = true;
//        private boolean enableRejectedContentText = true;
//        private boolean enableRejectedContentTitle = true;
//        private boolean enableRejectedIconSubtext = true;
//        private boolean enableRejectedLabelText = true;
//        private boolean enableCongratsSecondaryExitButton = true;
//        private boolean enablePendingSecondaryExitButton = true;
//        private boolean enableRejectedSecondaryExitButton = true;
//        private boolean enableApprovedReceipt = true;
//        private boolean enableApprovedAmount = true;
//        private boolean enableApprovedPaymentMethodInfo = true;
//
//        private Integer secondaryCongratsExitResultCode;
//        private Integer secondaryPendingExitResultCode;
//        private Integer secondaryRejectedExitResultCode;

        //Nuevo customizable
//
//        public Builder setApprovedTitle(String title) {
//            approvedTitle = title;
//            return this;
//        }
//
//        public Builder setRejectedTitle(String title) {
//            rejectedTitle = title;
//            return this;
//        }
//
//        public Builder setPendingTitle(String title) {
//            pendingTitle = title;
//            return this;
//        }
//
//        public Builder setApprovedLabelText(String label) {
//            approvedLabelText = label;
//            return this;
//        }
//
//        public Builder disableRejectedLabelText() {
//            enableRejectedLabelText = false;
//            return this;
//        }
//
//        public Builder setBadgeApproved(@Badge.ApprovedBadges String approvedBadge) {
//            this.approvedBadge = approvedBadge;
//            return this;
//        }
//
//        public Builder setApprovedHeaderIcon(@DrawableRes int headerIcon) {
//            approvedIcon = headerIcon;
//            return this;
//        }
//
//        public Builder setPendingHeaderIcon(@DrawableRes int headerIcon) {
//            pendingIcon = headerIcon;
//            return this;
//        }
//
//        public Builder setRejectedHeaderIcon(@DrawableRes int headerIcon) {
//            rejectedIcon = headerIcon;
//            return this;
//        }
//
//        public Builder setApprovedHeaderIcon(@NonNull String headerIconUrl) {
//            approvedUrlIcon = headerIconUrl;
//            return this;
//        }
//
//        public Builder setPendingHeaderIcon(@NonNull String headerIconUrl) {
//            pendingUrlIcon = headerIconUrl;
//            return this;
//        }
//
//        public Builder setRejectedHeaderIcon(@NonNull String headerIconUrl) {
//            rejectedUrlIcon = headerIconUrl;
//            return this;
//        }
//
//        //hasta acá
//        @Deprecated
//        public Builder setApprovedSubtitle(String subtitle) {
//            approvedSubtitle = subtitle;
//            return this;
//        }
//
//        @Deprecated
//        public Builder setPendingSubtitle(String subtitle) {
//            pendingSubtitle = subtitle;
//            return this;
//        }
//
//        @Deprecated
//        public Builder disableRejectedIconSubtext() {
//            enableRejectedIconSubtext = false;
//            enableRejectedLabelText = false;
//            return this;
//        }
//
//        @Deprecated
//        public Builder setRejectedSubtitle(String subtitle) {
//            rejectedSubtitle = subtitle;
//            return this;
//        }
//
//        @Deprecated
//        public Builder setRejectedIconSubtext(String text) {
//            rejectedIconSubtext = text;
//            return this;
//        }
//
//        @Deprecated
//        public Builder setTitleBackgroundColor(@ColorInt Integer titleBackgroundColor) {
//            this.titleBackgroundColor = titleBackgroundColor;
//            return this;
//        }
//
//        //body
//        public Builder setPendingContentTitle(String title) {
//            pendingContentTitle = title;
//            return this;
//        }
//
//        //body
//        public Builder setPendingContentText(String text) {
//            pendingContentText = text;
//            return this;
//        }
//
//        //body
//        public Builder disableApprovedPaymentMethodInfo() {
//            enableApprovedPaymentMethodInfo = false;
//            return this;
//        }
//
//        //body
//        public Builder disablePendingContentText() {
//            enablePendingContentText = false;
//            return this;
//        }
//
//        //body
//        public Builder disablePendingContentTitle() {
//            enablePendingContentTitle = false;
//            return this;
//        }
//
//        //body
//        public Builder disableRejectedContentText() {
//            enableRejectedContentText = false;
//            return this;
//        }
//
//        //body
//        public Builder disableRejectedContentTitle() {
//            enableRejectedContentTitle = false;
//            return this;
//        }
//
//        //body
//        public Builder disableRejectionRetry() {
//            rejectionRetryEnabled = false;
//            return this;
//        }
//
//        //body
//        public Builder setRejectedContentText(String text) {
//            rejectedContentText = text;
//            return this;
//        }
//
//        //body
//        public Builder setRejectedContentTitle(String title) {
//            rejectedContentTitle = title;
//            return this;
//        }
//
//        //body
//        public Builder disableApprovedReceipt() {
//            enableApprovedReceipt = false;
//            return this;
//        }
//
//        //body
//        public Builder disableApprovedAmount() {
//            enableApprovedAmount = false;
//            return this;
//        }
//
//        //footer
//        public Builder setRejectedSecondaryExitButton(String title, @NonNull Integer resultCode) {
//            secondaryRejectedExitButtonTitle = title;
//            secondaryRejectedExitResultCode = resultCode;
//            return this;
//        }
//
//        //footer
//        public Builder disableApprovedSecondaryExitButton() {
//            enableCongratsSecondaryExitButton = false;
//            return this;
//        }
//
//        //footer
//        public Builder disablePendingSecondaryExitButton() {
//            enablePendingSecondaryExitButton = false;
//            return this;
//        }
//
//        //footer
//        public Builder disableRejectedSecondaryExitButton() {
//            enableRejectedSecondaryExitButton = false;
//            return this;
//        }
//
//        @Deprecated
//        public Builder setPendingSecondaryExitButton(String title, PaymentResultCallback paymentResultCallback) {
//            secondaryPendingExitButtonTitle = title;
//            CallbackHolder.getInstance()
//                .addPaymentResultCallback(CallbackHolder.PENDING_PAYMENT_RESULT_CALLBACK, paymentResultCallback);
//            return this;
//        }
//
//        //footer
//        public Builder setExitButtonTitle(String title) {
//            exitButtonTitle = title;
//            return this;
//        }
//
//        //footer
//        @Deprecated
//        public Builder setApprovedSecondaryExitButton(String title, PaymentResultCallback paymentResultCallback) {
//            secondaryCongratsExitButtonTitle = title;
//            CallbackHolder.getInstance()
//                .addPaymentResultCallback(CallbackHolder.CONGRATS_PAYMENT_RESULT_CALLBACK, paymentResultCallback);
//            return this;
//        }
//
//        //footer
//        public Builder setApprovedSecondaryExitButton(String title, @NonNull Integer resultCode) {
//            secondaryCongratsExitButtonTitle = title;
//            secondaryCongratsExitResultCode = resultCode;
//            return this;
//        }
//
//        //footer
//        public Builder setPendingSecondaryExitButton(String title, @NonNull Integer resultCode) {
//            secondaryPendingExitButtonTitle = title;
//            secondaryPendingExitResultCode = resultCode;
//            return this;
//        }
//
//        @Deprecated
//        public Builder setRejectedSecondaryExitButton(String title, PaymentResultCallback paymentResultCallback) {
//            secondaryRejectedExitButtonTitle = title;
//            CallbackHolder.getInstance()
//                .addPaymentResultCallback(CallbackHolder.REJECTED_PAYMENT_RESULT_CALLBACK, paymentResultCallback);
//            return this;
//        }
//

        @NonNull
        public PaymentResultScreenConfiguration build() {
            return new PaymentResultScreenConfiguration(this);
        }

        /**
         * Custom fragment that will appear before payment method description
         * inside "congrats" result screen.
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
         * inside "congrats" result screen.
         *
         * @param zClass fragment class
         * @return builder
         */
        public Builder setBottomFragment(@NonNull final Class<? extends Fragment> zClass,
            @Nullable final Bundle args) {
            bottomFragment = new ExternalFragment(zClass, args);
            return this;
        }
    }
}
