package com.mercadopago.android.px.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class OneTapMetadata implements Parcelable, Serializable {

    @SerializedName("payment_method_id")
    private String paymentMethodId;
    @SerializedName("payment_type_id")
    private String paymentTypeId;
    private CardPaymentMetadata card;

    protected OneTapMetadata(final Parcel in) {
        paymentMethodId = in.readString();
        paymentTypeId = in.readString();
        card = in.readParcelable(CardPaymentMetadata.class.getClassLoader());
    }

    public static final Creator<OneTapMetadata> CREATOR = new Creator<OneTapMetadata>() {
        @Override
        public OneTapMetadata createFromParcel(final Parcel in) {
            return new OneTapMetadata(in);
        }

        @Override
        public OneTapMetadata[] newArray(final int size) {
            return new OneTapMetadata[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(paymentMethodId);
        dest.writeString(paymentTypeId);
        dest.writeParcelable(card, flags);
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public String getPaymentTypeId() {
        return paymentTypeId;
    }

    public CardPaymentMetadata getCard() {
        return card;
    }

    public boolean isValidOneTapType() {
        return PaymentTypes.isPlugin(paymentTypeId) ||
            (PaymentTypes.isCardPaymentType(paymentTypeId) && card != null);
    }
}
