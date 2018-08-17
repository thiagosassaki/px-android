package com.mercadopago.android.px.internal.features.review_and_confirm.components.items;

import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.ItemsModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReviewItemsTest {

    private static final int RESOURCE_ID = 1;

    @Mock ReviewItems.Props props;

    @Mock ItemsModel itemsModel;

    private ReviewItems renderer;

    @Before
    public void setUp() {
        renderer = new ReviewItems(props);
        when(props.getItemsModel()).thenReturn(itemsModel);
    }

    @Test
    public void whenIconIsAvailableInPreferenceAndIsUniqueItemThenShowIt() {
        when(itemsModel.hasUniqueItem()).thenReturn(true);
        when(props.getCollectorIcon()).thenReturn(RESOURCE_ID);

        final int icon = renderer.getIcon(props);
        assertEquals(RESOURCE_ID, icon);
    }

    @Test
    public void whenIconIsNotAvailableInPreferenceThenShowDefaultIcon() {
        when(itemsModel.hasUniqueItem()).thenReturn(true);
        when(props.getCollectorIcon()).thenReturn(null);

        final int icon = renderer.getIcon(props);
        assertEquals(R.drawable.px_review_item_default, icon);
    }

    @Test
    public void whenModelHasMultipleItemsThenShowDefaultIcon() {
        when(itemsModel.hasUniqueItem()).thenReturn(false);

        final int icon = renderer.getIcon(props);
        assertEquals(R.drawable.px_review_item_default, icon);
    }
}