package com.mercadopago.android.px.internal.features.codediscount;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mercadolibre.android.ui.widgets.MeliButton;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.textformatter.TextFormatter;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.model.Discount;
import javax.annotation.Nonnull;

class CongratsCodeDiscount extends CompactComponent<CongratsCodeDiscount.Props, Void> {

    /* default */ static class Props {

        @NonNull
        private final Discount discount;

        public Props(@NonNull final Discount discount) {
            this.discount = discount;
        }
    }

    @NonNull
    public final Action action;

    /* default */ CongratsCodeDiscount(@NonNull final Props props, @NonNull final Action action) {
        super(props);
        this.action = action;
    }

    public interface Action {
        void onButtonClicked();
    }

    @Override
    public View render(@Nonnull final ViewGroup parent) {
        final View mainContainer = inflate(parent, R.layout.px_view_congrats_discount);
        configureTitle(mainContainer);
        configureSubtitle(mainContainer);
        configureButton(mainContainer);
        return mainContainer;
    }

    private void configureTitle(final View mainContainer) {
        final TextView title = mainContainer.findViewById(R.id.title);
        title.setText(R.string.px_excellent);
    }

    private void configureSubtitle(final View mainContainer) {
        final TextView subtitle = mainContainer.findViewById(R.id.subtitle);
        if (props.discount.hasPercentOff()) {
            subtitle.setText(subtitle.getContext()
                    .getString(R.string.px_get_your_discount_percent, props.discount.getPercentOff()));
        } else {
            TextFormatter.withCurrencyId(props.discount.getCurrencyId())
                    .withSpace()
                    .amount(props.discount.getAmountOff())
                    .normalDecimals()
                    .into(subtitle)
                    .holder(R.string.px_get_your_discount_amount);
        }
    }

    private void configureButton(final View mainContainer) {
        final MeliButton button = mainContainer.findViewById(R.id.button);
        button.setText(R.string.px_continue_label);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                action.onButtonClicked();
            }
        });
    }
}
