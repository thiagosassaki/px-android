package com.mercadopago.android.px.model.requests;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import com.mercadopago.android.px.model.Payer;
import java.io.Serializable;

public class PaymentBodyIntent implements Serializable, Parcelable {

    @SerializedName("pref_id")
    private final String preferenceId;
    private final String publicKey;
    private final String paymentMethodId;
    private final String payerEmail;
    private String campaignId;
    private String couponAmount;
    private String couponCode;
    private boolean binaryMode;
    private Integer installments;
    private Long issuerId;
    @SerializedName("token")
    private String tokenId;
    private Payer payer;

    protected PaymentBodyIntent(final Builder builder) {
        preferenceId = builder.preferenceId;
        publicKey = builder.publicKey;
        paymentMethodId = builder.paymentMethodId;
        payerEmail = builder.payerEmail;
        campaignId = builder.campaignId;
        couponAmount = builder.couponAmount;
        couponCode = builder.couponCode;
        binaryMode = builder.binaryMode;
        installments = builder.installments;
        issuerId = builder.issuerId;
        tokenId = builder.tokenId;
        couponCode = builder.couponCode;
        binaryMode = builder.binaryMode;
        installments = builder.installments;
        issuerId = builder.issuerId;
        tokenId = builder.tokenId;
        payer = builder.payer;
    }

    protected PaymentBodyIntent(Parcel in) {
        preferenceId = in.readString();
        publicKey = in.readString();
        paymentMethodId = in.readString();
        payerEmail = in.readString();
        campaignId = in.readString();
        couponAmount = in.readString();
        couponCode = in.readString();
        binaryMode = in.readByte() != 0;
        installments = in.readInt();
        issuerId = in.readLong();
        tokenId = in.readString();
        payer = (Payer)in.readSerializable();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(preferenceId);
        dest.writeString(publicKey);
        dest.writeString(paymentMethodId);
        dest.writeString(payerEmail);
        dest.writeString(campaignId);
        dest.writeString(couponAmount);
        dest.writeString(couponCode);
        dest.writeByte((byte) (binaryMode ? 1 : 0));
        dest.writeInt(installments);
        dest.writeLong(issuerId);
        dest.writeString(tokenId);
        dest.writeSerializable(payer);
    }

    public static final Creator<PaymentBodyIntent> CREATOR = new Creator<PaymentBodyIntent>() {
        @Override
        public PaymentBodyIntent createFromParcel(Parcel in) {
            return new PaymentBodyIntent(in);
        }

        @Override
        public PaymentBodyIntent[] newArray(int size) {
            return new PaymentBodyIntent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public static class Builder {

        private final String preferenceId;
        private final String publicKey;
        private final String paymentMethodId;
        private final String payerEmail;
        private String campaignId;
        private String couponAmount;
        private String couponCode;
        private boolean binaryMode;
        private Integer installments;
        private Long issuerId;
        private String tokenId;
        private Payer payer;

        public Builder(final String preferenceId, final String publicKey, final String paymentMethodId,
            final String payerEmail) {
            this.preferenceId = preferenceId;
            this.publicKey = publicKey;
            this.paymentMethodId = paymentMethodId;
            this.payerEmail = payerEmail;
        }

        public PaymentBodyIntent.Builder setBinaryMode(final boolean binaryMode) {
            this.binaryMode = binaryMode;
            return this;
        }

        public PaymentBodyIntent.Builder setInstallments(final Integer installments) {
            this.installments = installments;
            return this;
        }

        public PaymentBodyIntent.Builder setIssuerId(final Long issuerId) {
            this.issuerId = issuerId;
            return this;
        }

        public PaymentBodyIntent.Builder setTokenId(final String tokenId) {
            this.tokenId = tokenId;
            return this;
        }

        public PaymentBodyIntent.Builder setCampaignId(final String campaignId) {
            this.campaignId = campaignId;
            return this;
        }

        public PaymentBodyIntent.Builder setCouponAmount(final String couponAmount) {
            this.couponAmount = couponAmount;
            return this;
        }

        public PaymentBodyIntent.Builder setCouponCode(final String couponCode) {
            this.couponCode = couponCode;
            return this;
        }

        public PaymentBodyIntent.Builder setPayer(final Payer payer) {
            this.payer = payer;
            return this;
        }

        public PaymentBodyIntent build() {
            return new PaymentBodyIntent(this);
        }
    }
}
