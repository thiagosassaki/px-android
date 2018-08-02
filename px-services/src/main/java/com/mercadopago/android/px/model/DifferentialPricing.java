package com.mercadopago.android.px.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import java.io.Serializable;

public class DifferentialPricing implements Serializable, Parcelable {

    /**
     * Differential pricing ID
     */
    @Nullable private final Integer id;

    public DifferentialPricing(@Nullable final Integer id) {
        this.id = id;
    }

    @Nullable
    public Integer getId() {
        return id;
    }

    protected DifferentialPricing(final Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
    }

    public static final Creator<DifferentialPricing> CREATOR = new Creator<DifferentialPricing>() {
        @Override
        public DifferentialPricing createFromParcel(final Parcel in) {
            return new DifferentialPricing(in);
        }

        @Override
        public DifferentialPricing[] newArray(final int size) {
            return new DifferentialPricing[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
    }
}
