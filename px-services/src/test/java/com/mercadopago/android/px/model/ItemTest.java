package com.mercadopago.android.px.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class ItemTest {

    private List<Item> items;
    private static final String MOCK_VALID_CURRENCY_ID_2 = "MXN";
    private static final String MOCK_VALID_CURRENCY_ID = "ARS";

    @Before
    public void setUp() {
        items = new ArrayList<>();
    }

    @Test
    public void whenListIsEmptyValidItemsIsFalse() {
        items.clear();
        assertFalse(Item.areItemsValid(items));
    }

    @Test
    public void whenItemsHaveOneValidItemValidItemsReturnTrue() {
        items.add(stubValidItem());
        assertTrue(Item.areItemsValid(items));
    }

    @Test
    public void whenItemsHaveMoreThanOneValidItemsWithSameCurrencyValidItemsReturnTrue() {
        items.add(stubValidItem());
        items.add(stubValidItem(20, 1));
        items.add(stubValidItem());
        items.add(stubValidItem());
        assertTrue(Item.areItemsValid(items));
    }

    @Test
    public void whenItemsHaveMoreThanOneValidItemsWithDifferentCurrencyValidItemsReturnFalse() {
        items.add(stubValidItem());
        items.add(stubValidItem());
        items.add(stubValidItemOtherCurrency());
        items.add(stubValidItem());
        assertFalse(Item.areItemsValid(items));
    }

    @Test
    public void whenItemsHaveInvalidThenValidItemsReturnFalse() {
        items.add(stubInvalidItem());
        assertFalse(Item.areItemsValid(items));
    }

    private Item stubValidItem() {
        return stubValidItem(10, 1);
    }

    private Item stubValidItem(int amount, int quantity) {
        Item item = new Item("SOME DESC", quantity, new BigDecimal(amount));
        item.setCurrencyId(MOCK_VALID_CURRENCY_ID);
        item.setId("SOME ID");
        return item;
    }

    private Item stubValidItemOtherCurrency() {
        Item item = new Item("SOME DESC", new BigDecimal(10));
        item.setCurrencyId(MOCK_VALID_CURRENCY_ID_2);
        item.setId("SOME ID");
        return item;
    }

    private Item stubInvalidItem() {
        Item item = new Item("SOME DESC", new BigDecimal(0));
        item.setCurrencyId("INVALID CURRENCY ID");
        item.setId("SOME ID");
        return item;
    }

    @Test
    public void whenThereAre10ItemsOfQuantity1AndAmount10ThenTotalAmountIs100() {
        for (int i = 0; i < 10; i++) {
            items.add(stubValidItem());
        }
        assertEquals(new BigDecimal(100), Item.getTotalAmountWith(items));
    }

    @Test
    public void whenThereAre10ItemsOfQuantity2AndAmount10ThenTotalAmountIs200() {
        for (int i = 0; i < 10; i++) {
            items.add(stubValidItem(10, 2));
        }
        assertEquals(new BigDecimal(200), Item.getTotalAmountWith(items));
    }

    @After
    public void cleanUp() {
        items.clear();
    }
}