package com.mercadopago.android.px.internal.features.review_and_confirm.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class TermsAndConditionsModel implements Parcelable {
    private final String termsAndConditionsUrl;
    private final String message;
    private final String messageLinked;
    private final String publicKey;
    private final LineSeparatorType lineSeparatorType;

    public TermsAndConditionsModel(final @NonNull String termsAndConditionsUrl,
        final @NonNull String message,
        final @NonNull String messageLinked,
        final @NonNull String publicKey,
        final @NonNull LineSeparatorType lineSeparatorType) {

        this.termsAndConditionsUrl = termsAndConditionsUrl;
        this.message = message;
        this.messageLinked = messageLinked;
        this.publicKey = publicKey;
        this.lineSeparatorType = lineSeparatorType;
    }

    protected TermsAndConditionsModel(Parcel in) {
        termsAndConditionsUrl = in.readString();
        message = in.readString();
        messageLinked = in.readString();
        publicKey = in.readString();
        lineSeparatorType = LineSeparatorType.valueOf(in.readString());
    }

    public static final Creator<TermsAndConditionsModel> CREATOR = new Creator<TermsAndConditionsModel>() {
        @Override
        public TermsAndConditionsModel createFromParcel(Parcel in) {
            return new TermsAndConditionsModel(in);
        }

        @Override
        public TermsAndConditionsModel[] newArray(int size) {
            return new TermsAndConditionsModel[size];
        }
    };

    public String getUrl() {
        return termsAndConditionsUrl;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageLinked() {
        return messageLinked;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public LineSeparatorType getLineSeparatorType() {
        return lineSeparatorType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(termsAndConditionsUrl);
        dest.writeString(message);
        dest.writeString(messageLinked);
        dest.writeString(publicKey);
        dest.writeString(lineSeparatorType.name());
    }
}
