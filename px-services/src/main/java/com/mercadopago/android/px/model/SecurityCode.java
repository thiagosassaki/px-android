package com.mercadopago.android.px.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.mercadopago.android.px.internal.util.ParcelableUtil;
import java.io.Serializable;

public class SecurityCode implements Parcelable, Serializable {

    private String cardLocation;
    private Integer length;
    private String mode;

    public String getCardLocation() {
        return cardLocation;
    }

    public void setCardLocation(String cardLocation) {
        this.cardLocation = cardLocation;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    protected SecurityCode(Parcel in) {
        cardLocation = in.readString();
        length = ParcelableUtil.getOptionalInteger(in);
        mode = in.readString();
    }

    public static final Creator<SecurityCode> CREATOR = new Creator<SecurityCode>() {
        @Override
        public SecurityCode createFromParcel(Parcel in) {
            return new SecurityCode(in);
        }

        @Override
        public SecurityCode[] newArray(int size) {
            return new SecurityCode[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(cardLocation);
        ParcelableUtil.writeOptional(dest, length);
        dest.writeString(mode);
    }
}
