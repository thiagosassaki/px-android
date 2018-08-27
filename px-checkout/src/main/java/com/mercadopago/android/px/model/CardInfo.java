package com.mercadopago.android.px.model;

/**
 * Created by vaserber on 10/24/16.
 */

public class CardInfo {

    private Integer cardNumberLength;
    private Integer securityCodeLength;
    private String securityCodeLocation;
    private final String lastFourDigits;
    private final String firstSixDigits;

    public CardInfo(final CardToken cardToken) {
        cardNumberLength = cardToken.getCardNumber().length();
        lastFourDigits = cardToken.getCardNumber().substring(cardNumberLength - 4, cardNumberLength);
        firstSixDigits = cardToken.getCardNumber().substring(0, 6);
    }

    public CardInfo(final Token token) {
        cardNumberLength = token.getCardNumberLength();
        lastFourDigits = token.getLastFourDigits();
        firstSixDigits = token.getFirstSixDigits();
    }

    public CardInfo(final Card card) {
        lastFourDigits = card.getLastFourDigits();
        firstSixDigits = card.getFirstSixDigits();
        securityCodeLength = card.getSecurityCodeLength();
        securityCodeLocation = card.getSecurityCodeLocation();
    }

    public static boolean canCreateCardInfo(final Token token) {
        return token.getCardNumberLength() != null && token.getLastFourDigits() != null
            && token.getFirstSixDigits() != null;
    }

    public Integer getCardNumberLength() {
        return cardNumberLength;
    }

    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public String getFirstSixDigits() {
        return firstSixDigits;
    }

    public Integer getSecurityCodeLength() {
        return securityCodeLength;
    }

    public String getSecurityCodeLocation() {
        return securityCodeLocation;
    }
}
