package com.mercadopago.android.px.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import com.mercadopago.android.px.internal.util.ParcelableUtil;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public class Item implements Serializable, Parcelable {

    @Nullable
    private final String id;

    @NonNull
    private final String title;

    @Nullable
    private final String description;

    @Nullable
    private final String pictureUrl;

    @Nullable
    private final String categoryId;

    @NonNull
    private final Integer quantity;

    @NonNull
    private final BigDecimal unitPrice;

    @Nullable
    private String currencyId;

    /* default */
    protected Item(final Builder builder) {
        id = builder.id;
        title = builder.title;
        description = builder.description;
        pictureUrl = builder.pictureUrl;
        categoryId = builder.categoryId;
        quantity = builder.quantity;
        unitPrice = builder.unitPrice;
    }

    @Nullable
    public String getId() {
        return id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @Nullable
    public String getPictureUrl() {
        return pictureUrl;
    }

    @Nullable
    public String getCategoryId() {
        return categoryId;
    }

    @NonNull
    public Integer getQuantity() {
        return quantity;
    }

    @Nullable
    public String getCurrencyId() {
        return currencyId;
    }

    @NonNull
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public static BigDecimal getItemTotalAmount(@NonNull final Item item) {
        return item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()));
    }

    public boolean hasCardinality() {
        return quantity > 1;
    }

    public static boolean areItemsValid(@NonNull final Collection<Item> items) {
        return !items.isEmpty() && isEachItemValid(items);
    }

    private static boolean isEachItemValid(@NonNull final Iterable<Item> items) {
        boolean areAllValid = true;
        for (final Item item : items) {
            areAllValid = areAllValid && item.isItemValid();
        }
        return areAllValid;
    }

    private boolean isItemValid() {
        return BigDecimal.ZERO.compareTo(getUnitPrice()) < 0
            && getQuantity() > 0;
    }

    public static BigDecimal getTotalAmountWith(@NonNull final Iterable<Item> items) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (final Item item : items) {
            totalAmount = totalAmount.add(getItemTotalAmount(item));
        }
        return totalAmount;
    }

    public static String getItemsTitle(@NonNull @Size(min = 1) final List<Item> items, final String multipleDefault) {
        return items.size() > 1 ? multipleDefault : items.get(0).getTitle();
    }

    private Item(final Parcel in) {
        title = in.readString();
        quantity = ParcelableUtil.getOptionalInteger(in);
        unitPrice = ParcelableUtil.getOptionalBigDecimal(in);
        id = in.readString();
        description = in.readString();
        categoryId = in.readString();
        pictureUrl = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(final Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(final int size) {
            return new Item[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(title);
        ParcelableUtil.writeOptional(dest, quantity);
        ParcelableUtil.writeOptional(dest, unitPrice);
        dest.writeString(id);
        dest.writeString(description);
        dest.writeString(categoryId);
        dest.writeString(pictureUrl);
    }

    @SuppressWarnings("unused")
    public static final class Builder {
        /* default */ @NonNull final String title;
        /* default */ @NonNull final Integer quantity;
        /* default */ @NonNull final BigDecimal unitPrice;
        /* default */ @Nullable String id;
        /* default */ @Nullable String description;
        /* default */ @Nullable String categoryId;
        /* default */ @Nullable String pictureUrl;

        public Builder(@NonNull final String title, @NonNull final Integer quantity,
            @NonNull final BigDecimal unitPrice) {
            this.title = title;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public Builder setId(@NonNull final String id) {
            this.id = id;
            return this;
        }

        public Builder setDescription(@NonNull final String description) {
            this.description = description;
            return this;
        }

        public Builder setCategoryId(@NonNull final String categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public Builder setPictureUrl(@NonNull final String pictureUrl) {
            this.pictureUrl = pictureUrl;
            return this;
        }

        public Item build() {
            return new Item(this);
        }
    }
}
