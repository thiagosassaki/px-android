package com.mercadopago.android.px.components;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.util.ViewUtils;
import com.mercadopago.android.px.util.textformatter.TextFormatter;

import java.util.Locale;

import javax.annotation.Nonnull;

public class DiscountDetail extends CompactComponent<DiscountDetail.Props, Void> {

    public static class Props {
        @NonNull
        private final DiscountRepository discountRepository;

        public Props(@NonNull final DiscountRepository discountRepository) {
            this.discountRepository = discountRepository;
        }
    }

    public DiscountDetail(final Props props) {
        super(props);
    }

    @Override
    public View render(@Nonnull final ViewGroup parent) {
        final View mainContainer = inflate(parent, R.layout.px_view_discount_detail);
        configureSubtitleMessage(mainContainer);
        configureDetailMessage(mainContainer);
        configureSubDetailsMessage(mainContainer);
        return mainContainer;
    }

    private void configureSubDetailsMessage(View mainContainer) {
        if (props.discountRepository.isNotAvailableDiscount()) {
            mainContainer.findViewById(R.id.px_discount_detail_line).setVisibility(View.GONE);
            mainContainer.findViewById(R.id.px_discount_sub_details).setVisibility(View.GONE);
        }
    }

    private void configureSubtitleMessage(final View mainContainer) {
        final TextView subtitleMessage = mainContainer.findViewById(R.id.subtitle);
        if (isMaxCouponAmountApplicable()) {
            TextFormatter.withCurrencyId(props.discountRepository.getDiscount().getCurrencyId())
                .withSpace()
                .amount(props.discountRepository.getCampaign().getMaxCouponAmount())
                .normalDecimals()
                .into(subtitleMessage)
                .holder(R.string.px_max_coupon_amount);
        } else {
            subtitleMessage.setVisibility(View.GONE);
        }
    }

    private void configureDetailMessage(final View mainContainer) {
        final TextView detailTextView = mainContainer.findViewById(R.id.detail);
        if (props.discountRepository.isNotAvailableDiscount()) {
            configureNotAvailableDiscountDetail(detailTextView, mainContainer);
        } else if (isMaxCouponAmountApplicable()) {
            if (isAlwaysOnApplicable()) {
                setDetailMessage(detailTextView, R.string.px_always_on_discount_detail, mainContainer);
            } else {
                setDetailMessage(detailTextView, R.string.px_one_shot_discount_detail, mainContainer);
            }
        } else {
            detailTextView.setVisibility(View.GONE);
        }
    }

    private void configureNotAvailableDiscountDetail(final TextView detailTextView, final View mainContainer) {
        setDetailMessage(detailTextView, R.string.px_used_up_discount_detail, mainContainer);
        ViewUtils.setMarginBottomInView(detailTextView,
            mainContainer.getContext().getResources().getDimensionPixelSize(R.dimen.px_xxs_margin));
    }

    private void setDetailMessage(TextView detailTextView, int detailId, View view) {
        String detailMessage = view.getResources().getString(detailId);

        if (isEndDateApplicable()) {
            String endDateMessage = view.getResources().getString(R.string.px_discount_detail_end_date,
                props.discountRepository.getCampaign().getPrettyEndDate());
            detailTextView.setText(String.format(Locale.getDefault(), "%s %s", detailMessage, endDateMessage));
        } else {
            detailTextView.setText(detailMessage);
        }
    }

    @VisibleForTesting
        /* default */ boolean isEndDateApplicable() {
        return props.discountRepository.hasValidDiscount() ? props.discountRepository.getCampaign().hasEndDate() &&
            !props.discountRepository.isNotAvailableDiscount() : false;
    }

    @VisibleForTesting
        /* default */ boolean isMaxCouponAmountApplicable() {
        return props.discountRepository.hasValidDiscount() ?
            props.discountRepository.getCampaign().hasMaxCouponAmount() &&
                !props.discountRepository.isNotAvailableDiscount()
            : false;
    }

    @VisibleForTesting
        /* default */ boolean isAlwaysOnApplicable() {
        return props.discountRepository.hasValidDiscount() ?
            props.discountRepository.getCampaign().isAlwaysOnDiscount() &&
                !props.discountRepository.isNotAvailableDiscount()
            : false;
    }
}
