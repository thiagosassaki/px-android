package com.mercadopago.android.px.model;

import com.mercadopago.android.px.internal.features.CheckoutActivity;
import com.mercadopago.android.px.test.BaseTest;
import com.mercadopago.android.px.test.StaticMock;

public class CardTest extends BaseTest<CheckoutActivity> {

    public CardTest() {
        super(CheckoutActivity.class);
    }

    public void testIsSecurityCodeRequired() {

        Card card = StaticMock.getCard();

        assertTrue(card.isSecurityCodeRequired());
    }

    public void testIsSecurityCodeRequiredNull() {

        Card card = StaticMock.getCard();
        card.setSecurityCode(null);
        assertTrue(!card.isSecurityCodeRequired());
    }

    public void testIsSecurityCodeRequiredLengthZero() {

        Card card = StaticMock.getCard();
        card.getSecurityCode().setLength(0);
        assertTrue(!card.isSecurityCodeRequired());
    }
}
