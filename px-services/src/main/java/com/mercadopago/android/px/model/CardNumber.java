package com.mercadopago.android.px.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.mercadopago.android.px.internal.util.ParcelableUtil;
import java.io.Serializable;

public class CardNumber implements Parcelable, Serializable {

    private Integer length;
    private String validation;

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    protected CardNumber(Parcel in) {
        length = ParcelableUtil.getOptionalInteger(in);
        validation = in.readString();
    }

    public static final Creator<CardNumber> CREATOR = new Creator<CardNumber>() {
        @Override
        public CardNumber createFromParcel(Parcel in) {
            return new CardNumber(in);
        }

        @Override
        public CardNumber[] newArray(int size) {
            return new CardNumber[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        ParcelableUtil.writeOptional(dest, length);
        dest.writeString(validation);
    }
}
