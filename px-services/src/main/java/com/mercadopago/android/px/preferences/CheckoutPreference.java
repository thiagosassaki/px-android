package com.mercadopago.android.px.preferences;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import com.google.gson.annotations.SerializedName;
import com.mercadopago.android.px.model.DifferentialPricing;
import com.mercadopago.android.px.model.Item;
import com.mercadopago.android.px.model.Payer;
import com.mercadopago.android.px.model.Site;
import com.mercadopago.android.px.model.Sites;
import com.mercadopago.android.px.services.exceptions.CheckoutPreferenceException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mercadopago.android.px.services.util.TextUtil.isEmpty;

/**
 * Model that represents curl -X OPTIONS "https://api.mercadopago.com/checkout/preferences" | json_pp
 * It can be not exactly the same because exists custom configurations for open Preference.
 */
public class CheckoutPreference implements Serializable {

    /**
     * When the preference comes from backend then
     * id is received - Custom created CheckoutPreferences have null id.
     */
    @SuppressWarnings("UnusedDeclaration")
    private String id;
    @SuppressWarnings("UnusedDeclaration")
    private String siteId;

    @Nullable
    @SerializedName("differential_pricing")
    private DifferentialPricing differentialPricing;

    @NonNull
    private List<Item> items;

    private Payer payer;

    @SerializedName("payment_methods")
    private PaymentPreference paymentPreference;

    private Date expirationDateTo;
    private Date expirationDateFrom;
    private Site localPreferenceSite;
    //region support external integrations - payment processor instores
    private BigDecimal marketplaceFee;
    private BigDecimal shippingCost;
    private String operationType;
    private BigDecimal conceptAmount;
    private String conceptId;
    //endregion support external integrations

    CheckoutPreference(final Builder builder) {
        items = builder.items;
        expirationDateFrom = builder.expirationDateFrom;
        expirationDateTo = builder.expirationDateTo;
        localPreferenceSite = builder.localPreferenceSite;
        marketplaceFee = builder.marketplaceFee;
        shippingCost = builder.shippingCost;
        operationType = builder.operationType;
        differentialPricing = builder.differentialPricing;
        conceptAmount = builder.conceptAmount;
        conceptId = builder.conceptId;
        payer = getPayer(builder);
        final PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentTypeIds(builder.excludedPaymentTypes);
        paymentPreference.setExcludedPaymentMethodIds(builder.excludedPaymentMethods);
        paymentPreference.setMaxAcceptedInstallments(builder.maxInstallments);
        paymentPreference.setDefaultInstallments(builder.defaultInstallments);
        this.paymentPreference = paymentPreference;
    }

    @NonNull
    private Payer getPayer(final Builder builder) {
        final Payer payer = new Payer();
        payer.setEmail(builder.payerEmail);
        return payer;
    }

    public void validate() throws CheckoutPreferenceException {
        if (!Item.validItems(items)) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.INVALID_ITEM);
        } else if (!hasEmail()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.NO_EMAIL_FOUND);
        } else if (isExpired()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.EXPIRED_PREFERENCE);
        } else if (!isActive()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.INACTIVE_PREFERENCE);
        } else if (!validInstallmentsPreference()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.INVALID_INSTALLMENTS);
        } else if (!validPaymentTypeExclusion()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.EXCLUDED_ALL_PAYMENT_TYPES);
        }
    }

    private boolean hasEmail() {
        return payer != null && !isEmpty(payer.getEmail());
    }

    public boolean validPaymentTypeExclusion() {
        return paymentPreference == null || paymentPreference.excludedPaymentTypesValid();
    }

    public boolean validInstallmentsPreference() {
        return paymentPreference == null || paymentPreference.installmentPreferencesValid();
    }

    public Boolean isExpired() {
        Date date = new Date();
        return expirationDateTo != null && date.after(expirationDateTo);
    }

    public Boolean isActive() {
        Date date = new Date();
        return expirationDateFrom == null || date.after(expirationDateFrom);
    }

    public String getSiteId() {
        return siteId;
    }

    //region support external integrations - payment processor instores
    @SuppressWarnings("unused")
    public String getOperationType() {
        return operationType;
    }

    @SuppressWarnings("unused")
    public BigDecimal getMarketplaceFee() {
        return marketplaceFee;
    }

    @SuppressWarnings("unused")
    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    @SuppressWarnings("unused")
    public Site getLocalPreferenceSite() {
        return localPreferenceSite;
    }

    @Nullable
    public DifferentialPricing getDifferentialPricing() {
        return differentialPricing;
    }

    @SuppressWarnings("unused")
    public BigDecimal getConceptAmount() {
        return conceptAmount;
    }

    @SuppressWarnings("unused")
    public String getConceptId() {
        return conceptId;
    }
    //endregion support external integrations

    /**
     * Sum of value * quantity of listed items in a preference.
     *
     * @return items total amount
     */
    public BigDecimal getTotalAmount() {
        return Item.getTotalAmountWith(items);
    }

    @NonNull
    public List<String> getExcludedPaymentTypes() {
        if (paymentPreference != null) {
            return paymentPreference.getExcludedPaymentTypes();
        } else {
            return new ArrayList<>();
        }
    }

    public Site getSite() {
        Site site;
        if (localPreferenceSite == null) {
            site = Sites.getById(siteId);
        } else {
            site = localPreferenceSite;
        }
        return site;
    }

    @Size(min = 1)
    @NonNull
    public List<Item> getItems() {
        return items;
    }

    @NonNull
    public Payer getPayer() {
        return payer;
    }

    @SuppressWarnings("unused")
    public Integer getMaxInstallments() {
        if (paymentPreference != null) {
            return paymentPreference.getMaxInstallments();
        } else {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public Integer getDefaultInstallments() {
        if (paymentPreference != null) {
            return paymentPreference.getDefaultInstallments();
        } else {
            return null;
        }
    }

    public List<String> getExcludedPaymentMethods() {
        if (paymentPreference != null) {
            return paymentPreference.getExcludedPaymentMethodIds();
        } else {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public Date getExpirationDateFrom() {
        return expirationDateFrom;
    }

    @SuppressWarnings("unused")
    public Date getExpirationDateTo() {
        return expirationDateTo;
    }

    @SuppressWarnings("unused")
    public String getDefaultPaymentMethodId() {
        if (paymentPreference != null) {
            return paymentPreference.getDefaultPaymentMethodId();
        } else {
            return null;
        }
    }

    public PaymentPreference getPaymentPreference() {
        // If payment preference does not exists create one.
        if (paymentPreference == null) {
            paymentPreference = new PaymentPreference();
        }
        return paymentPreference;
    }

    public String getId() {
        return id;
    }

    public static class Builder {
        //region mandatory params
        private final List<Item> items;
        private final Site localPreferenceSite;
        private final String payerEmail;
        //endregion mandatory params
        private final List<String> excludedPaymentMethods;
        private final List<String> excludedPaymentTypes;
        private Integer maxInstallments;
        private Integer defaultInstallments;
        private Date expirationDateTo;
        private Date expirationDateFrom;
        private BigDecimal marketplaceFee;
        private BigDecimal shippingCost;
        private String operationType;
        /* default */ @Nullable DifferentialPricing differentialPricing;
        private BigDecimal conceptAmount;
        private String conceptId;

        /**
         * Builder for custom CheckoutPreference construction
         *
         * @param site preference site
         * @param payerEmail payer email
         * @param items items to pay
         */
        public Builder(@NonNull final Site site, @NonNull final String payerEmail,
            @Size(min = 1) @NonNull final List<Item> items) {
            this.items = items;
            this.payerEmail = payerEmail;
            localPreferenceSite = site;
            excludedPaymentMethods = new ArrayList<>();
            excludedPaymentTypes = new ArrayList<>();
        }

        @SuppressWarnings("unused")
        public Builder addExcludedPaymentMethod(@NonNull final String paymentMethodId) {
            excludedPaymentMethods.add(paymentMethodId);
            return this;
        }

        @SuppressWarnings("unused")
        public Builder addExcludedPaymentMethods(@NonNull final List<String> paymentMethodIds) {
            excludedPaymentMethods.addAll(paymentMethodIds);
            return this;
        }

        @SuppressWarnings("unused")
        public Builder addExcludedPaymentType(@NonNull final String paymentTypeId) {
            excludedPaymentTypes.add(paymentTypeId);
            return this;
        }

        @SuppressWarnings("unused")
        public Builder addExcludedPaymentTypes(@NonNull final List<String> paymentTypeIds) {
            excludedPaymentTypes.addAll(paymentTypeIds);
            return this;
        }

        @SuppressWarnings("unused")
        public Builder setMaxInstallments(final Integer maxInstallments) {
            this.maxInstallments = maxInstallments;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder setDefaultInstallments(final Integer defaultInstallments) {
            this.defaultInstallments = defaultInstallments;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder setExpirationDate(final Date date) {
            expirationDateTo = date;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder setActiveFrom(final Date date) {
            expirationDateFrom = date;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder setMarketplaceFee(final BigDecimal marketplaceFee) {
            this.marketplaceFee = marketplaceFee;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder setShippingCost(final BigDecimal shippingCost) {
            this.shippingCost = shippingCost;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder setOperationType(final String operationType) {
            this.operationType = operationType;
            return this;
        }

        /**
         * Differential pricing configuration for this preference.
         *
         * @param differentialPricing differential pricing object
         * @return builder
         */
        public Builder setDifferentialPricing(@Nullable final DifferentialPricing differentialPricing) {
            this.differentialPricing = differentialPricing;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder setConceptAmount(final BigDecimal conceptAmount) {
            this.conceptAmount = conceptAmount;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder setConceptId(final String conceptId) {
            this.conceptId = conceptId;
            return this;
        }

        @SuppressWarnings("unused")
        public CheckoutPreference build() {
            return new CheckoutPreference(this);
        }
    }
}
