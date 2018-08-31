package com.mercadopago.android.px.internal.features.explode;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

public class ExplodeParams implements Parcelable, Serializable {

    private int buttonHeightInPixels;
    private int buttonLeftRightMarginInPixels;
    private int yButtonPositionInPixels;
    private String buttonText;
    private int paymentTimeout;

    public ExplodeParams(final int yButtonPositionInPixels, final int buttonHeightInPixels,
        final int buttonLeftRightMarginInPixels, final String buttonText, final int paymentTimeout) {
        this.buttonHeightInPixels = buttonHeightInPixels;
        this.yButtonPositionInPixels = yButtonPositionInPixels;
        this.buttonLeftRightMarginInPixels = buttonLeftRightMarginInPixels;
        this.buttonText = buttonText;
        this.paymentTimeout = paymentTimeout;
    }

    protected ExplodeParams(Parcel in) {
        buttonHeightInPixels = in.readInt();
        buttonLeftRightMarginInPixels = in.readInt();
        yButtonPositionInPixels = in.readInt();
        buttonText = in.readString();
        paymentTimeout = in.readInt();
    }

    public static final Creator<ExplodeParams> CREATOR = new Creator<ExplodeParams>() {
        @Override
        public ExplodeParams createFromParcel(Parcel in) {
            return new ExplodeParams(in);
        }

        @Override
        public ExplodeParams[] newArray(int size) {
            return new ExplodeParams[size];
        }
    };

    public int getButtonHeightInPixels() {
        return buttonHeightInPixels;
    }

    public int getButtonLeftRightMarginInPixels() {
        return buttonLeftRightMarginInPixels;
    }

    public int getyButtonPositionInPixels() {
        return yButtonPositionInPixels;
    }

    public String getButtonText() {
        return buttonText;
    }

    public int getPaymentTimeout() {
        return paymentTimeout;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(buttonHeightInPixels);
        dest.writeInt(buttonLeftRightMarginInPixels);
        dest.writeInt(yButtonPositionInPixels);
        dest.writeString(buttonText);
        dest.writeInt(paymentTimeout);
    }
}
