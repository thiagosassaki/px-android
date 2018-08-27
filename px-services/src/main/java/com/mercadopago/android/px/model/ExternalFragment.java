package com.mercadopago.android.px.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import java.io.Serializable;

public class ExternalFragment implements Parcelable, Serializable {

    @NonNull
    public final String zClassName;
    @Nullable
    public final Bundle args;

    public ExternalFragment(@NonNull final Class<? extends Fragment> zClass, @Nullable final Bundle args) {
        zClassName = zClass.getName();
        this.args = args;
    }

    protected ExternalFragment(final Parcel in) {
        zClassName = in.readString();
        args = in.readBundle(getClass().getClassLoader());
    }

    public static final Creator<ExternalFragment> CREATOR = new Creator<ExternalFragment>() {
        @Override
        public ExternalFragment createFromParcel(final Parcel in) {
            return new ExternalFragment(in);
        }

        @Override
        public ExternalFragment[] newArray(final int size) {
            return new ExternalFragment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(zClassName);
        dest.writeBundle(args);
    }
}
