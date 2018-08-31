package com.mercadopago.android.px.internal.features.onetap.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.util.textformatter.TextFormatter;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.model.Item;
import javax.annotation.Nonnull;

class DetailItem extends CompactComponent<DetailItem.Props, Void> {

    /* default */ static class Props {

        /* default */ @NonNull final Item item;

        /* default */ @NonNull final String currencyId;

        /* default */ Props(@NonNull final Item item, @NonNull final String currencyId) {
            this.item = item;
            this.currencyId = currencyId;
        }
    }

    DetailItem(@NonNull final Props props) {
        super(props);
    }

    @Override
    public View render(@Nonnull final ViewGroup parent) {
        final View row = inflate(parent, R.layout.px_view_onetap_item_detail_row);
        final TextView title = row.findViewById(R.id.title);
        ViewUtils.loadOrGone(resolveItemTitle(parent.getContext()), title);
        final TextView description = row.findViewById(R.id.description);
        ViewUtils.loadOrGone(props.item.getDescription(), description);

        final TextView itemAmount = row.findViewById(R.id.item_amount);
        TextFormatter.withCurrencyId(props.currencyId)
            .withSpace()
            .amount(Item.getItemTotalAmount(props.item))
            .normalDecimals()
            .into(itemAmount)
            .normal();

        return row;
    }

    private String resolveItemTitle(final Context context) {
        return props.item.hasCardinality() ? context
            .getString(R.string.px_quantity_modal, props.item.getQuantity(), props.item.getTitle()) :
            props.item.getTitle();
    }
}
