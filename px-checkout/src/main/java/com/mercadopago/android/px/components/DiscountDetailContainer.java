package com.mercadopago.android.px.components;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.android.px.R;
import com.mercadopago.android.px.customviews.MPTextView;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.util.textformatter.TextFormatter;

import javax.annotation.Nonnull;

public class DiscountDetailContainer extends CompactComponent<DiscountDetailContainer.Props, Void> {

    public static final class Props {
        @NonNull
        /* default */ final DialogTitleType dialogTitleType;
        @NonNull
        /* default */ final Discount discount;
        @NonNull
        /* default */ final Campaign campaign;


        public Props(@NonNull final DialogTitleType dialogTitleType, @NonNull final Discount discount, @NonNull final Campaign campaign) {
            this.dialogTitleType = dialogTitleType;
            this.discount = discount;
            this.campaign = campaign;
        }

        public enum DialogTitleType {
            BIG,SMALL
        }
    }

    public DiscountDetailContainer(@NonNull final DiscountDetailContainer.Props props) {
        super(props);
    }

    @Override
    public View render(@Nonnull ViewGroup parent) {
        addDiscountTitle(parent);
        addDiscountDetail(parent);
        return null;
    }

    private void addDiscountDetail(@NonNull final ViewGroup parent) {
        final View discountView =
                new DiscountDetail(new DiscountDetail.Props(props.discount, props.campaign))
                        .render(parent);

        parent.addView(discountView);
    }

    private void addDiscountTitle(final ViewGroup parent) {
        MPTextView title = getTitleTextView(parent);
        configureOffMessage(title);

        parent.addView(title);
    }

    private void configureOffMessage(final MPTextView textView) {
        if (props.discount.hasPercentOff()) {
            textView.setText(textView.getContext()
                    .getString(R.string.px_discount_percent_off, props.discount.getPercentOff()));
        } else {
            TextFormatter.withCurrencyId(props.discount.getCurrencyId())
                    .withSpace()
                    .amount(props.discount.getAmountOff())
                    .normalDecimals()
                    .into(textView)
                    .holder(R.string.px_discount_amount_off);
        }
    }

    private MPTextView getTitleTextView(final ViewGroup parent) {
        return props.dialogTitleType.equals(Props.DialogTitleType.BIG) ? (MPTextView) inflate(parent, R.layout.px_view_big_modal_title)
                : (MPTextView) inflate(parent, R.layout.px_view_small_modal_title);
    }
}

