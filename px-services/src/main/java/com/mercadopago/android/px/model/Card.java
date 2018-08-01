package com.mercadopago.android.px.model;

import android.support.annotation.Nullable;
import java.util.Date;

public class Card implements CardInformation {

    public static final Integer CARD_DEFAULT_SECURITY_CODE_LENGTH = 4;
    public static final String CARD_DEFAULT_SECURITY_CODE_LOCATION = "back";
    public static final Integer CARD_NUMBER_MAX_LENGTH = 16;

    private Cardholder cardHolder;
    private String customerId;
    private Date dateCreated;
    private Date dateLastUpdated;
    private Integer expirationMonth;
    private Integer expirationYear;
    private String firstSixDigits;
    private String id;
    private Issuer issuer;
    private String lastFourDigits;
    private PaymentMethod paymentMethod;
    private SecurityCode securityCode;

    @Override
    public Cardholder getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(Cardholder cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateLastUpdated() {
        return dateLastUpdated;
    }

    public void setDateLastUpdated(Date dateLastUpdated) {
        this.dateLastUpdated = dateLastUpdated;
    }

    @Override
    public Integer getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(Integer expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    @Override
    public Integer getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(Integer expirationYear) {
        this.expirationYear = expirationYear;
    }

    @Override
    public String getFirstSixDigits() {
        return firstSixDigits;
    }

    public void setFirstSixDigits(String firstSixDigits) {
        this.firstSixDigits = firstSixDigits;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Nullable
    public Issuer getIssuer() {
        return issuer;
    }

    public void setIssuer(@Nullable final Issuer issuer) {
        this.issuer = issuer;
    }

    @Nullable
    @Override
    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public void setLastFourDigits(@Nullable final String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(@Nullable PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public SecurityCode getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(@Nullable final SecurityCode securityCode) {
        this.securityCode = securityCode;
    }

    public boolean isSecurityCodeRequired() {
        if (securityCode != null) {
            return securityCode.getLength() != 0;
        } else {
            return false;
        }
    }

    @Override
    public Integer getSecurityCodeLength() {
        return securityCode != null ? securityCode.getLength() : CARD_DEFAULT_SECURITY_CODE_LENGTH;
    }

    public String getSecurityCodeLocation() {
        return securityCode != null ? securityCode.getCardLocation() : CARD_DEFAULT_SECURITY_CODE_LOCATION;
    }

    @Override
    public String toString() {
        return "Card{" +
            "cardHolder=" + cardHolder +
            ", customerId='" + customerId + '\'' +
            ", dateCreated=" + dateCreated +
            ", dateLastUpdated=" + dateLastUpdated +
            ", expirationMonth=" + expirationMonth +
            ", expirationYear=" + expirationYear +
            ", firstSixDigits='" + firstSixDigits + '\'' +
            ", id='" + id + '\'' +
            ", issuer=" + issuer +
            ", lastFourDigits='" + lastFourDigits + '\'' +
            ", paymentMethod=" + paymentMethod +
            ", securityCode=" + securityCode +
            '}';
    }
}