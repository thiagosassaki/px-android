package com.mercadopago.android.px.model;

import android.support.annotation.NonNull;
import android.support.annotation.Size;
import com.mercadopago.android.px.internal.util.CurrenciesUtil;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class Item implements Serializable {

    private String categoryId;
    private String currencyId;
    private String description;
    private String id;
    private String pictureUrl;
    private Integer quantity;
    private String title;
    private BigDecimal unitPrice;

    public Item(String description, Integer quantity, BigDecimal unitPrice) {
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Item(String description, Integer quantity, BigDecimal unitPrice, String pictureUrl) {

        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.pictureUrl = pictureUrl;
    }

    public Item(String description, BigDecimal amount) {

        this.description = description;
        quantity = 1;
        unitPrice = amount;
    }

    public Item(String description, BigDecimal amount, String pictureUrl) {
        this.description = description;
        quantity = 1;
        unitPrice = amount;
        this.pictureUrl = pictureUrl;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(final BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public boolean hasCardinality() {
        return quantity != null && quantity > 1;
    }

    private boolean isValid() {
        return BigDecimal.ZERO.compareTo(getUnitPrice()) < 0
            && getQuantity() != null
            && getQuantity() > 0
            && CurrenciesUtil.isValidCurrency(getCurrencyId())
            && getId() != null;
    }

    public static BigDecimal getItemTotalAmount(@NonNull final Item item) {
        return item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()));
    }

    private static boolean haveAllSameCurrency(@Size(min = 1) @NonNull final List<Item> items) {
        boolean areAllTheSameCurrency = true;
        final String currencyId = items.get(0).getCurrencyId();
        for (int i = 1; i < items.size(); i++) {
            areAllTheSameCurrency = areAllTheSameCurrency && currencyId.equals(items.get(i).getCurrencyId());
        }
        return areAllTheSameCurrency;
    }

    private static boolean eachIsValid(@NonNull final Iterable<Item> items) {
        boolean areAllValid = true;
        for (final Item item : items) {
            areAllValid = areAllValid && item.isValid();
        }
        return areAllValid;
    }

    public static boolean validItems(@NonNull final List<Item> items) {
        return !items.isEmpty() && eachIsValid(items) && haveAllSameCurrency(items);
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
}
