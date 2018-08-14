package com.mercadopago.android.px.views;

import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.Site;
import com.mercadopago.android.px.util.textformatter.TextFormatter;

import java.math.BigDecimal;

public class AmountView extends LinearLayoutCompat {

    /* default */
    @Nullable
    OnClick callback;

    private TextView amountDescription;
    private View amountContainer;
    private TextView amountBeforeDiscount;
    private TextView maxCouponAmount;
    private TextView finalAmount;
    private View line;
    private View arrow;
    private View mainContainer;

    public interface OnClick {

        void onDetailClicked();

        void onInputRequestClicked();
    }

    public AmountView(@NonNull final Context context) {
        super(context);
        init();
    }

    public void setOnClickListener(final OnClick callback) {
        this.callback = callback;
    }

    public AmountView(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AmountView(@NonNull final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void show(@NonNull final DiscountRepository discountRepository,
        @NonNull final BigDecimal totalAmount, @NonNull final Site site) {
        final Discount discount = discountRepository.getDiscount();
        final Campaign campaign = discountRepository.getCampaign();

        if (discountRepository.isNotAvailableDiscount()) {
            showNotAvailableDiscount(totalAmount, site);
        } else if (discountRepository.hasValidDiscount()) {
            show(discount, campaign, totalAmount, site);
        } else if (discountRepository.hasCodeCampaign()) {
            showCouponInput(totalAmount, site);
        } else {
            show(totalAmount, site);
        }
    }

    private void init() {
        inflate(getContext(), R.layout.px_amount_layout, this);
        mainContainer = findViewById(R.id.main_container);
        line = findViewById(R.id.line);
        amountDescription = findViewById(R.id.amount_description);
        amountBeforeDiscount = findViewById(R.id.amount_before_discount);
        finalAmount = findViewById(R.id.final_amount);
        maxCouponAmount = findViewById(R.id.max_coupon_amount);
        arrow = findViewById(R.id.blue_arrow);
        amountContainer = findViewById(R.id.amount_container);
        amountBeforeDiscount.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        configureElevation();
    }

    private void configureElevation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(getContext().getResources().getDimension(R.dimen.px_xxs_margin));
            line.setVisibility(GONE);
        } else {
            line.setVisibility(VISIBLE);
        }
    }

    private void show(@NonNull final Discount discount,
        @NonNull final Campaign campaign,
        @NonNull final BigDecimal totalAmount,
        @NonNull final Site site) {

        showDiscount(discount, campaign, totalAmount, site);
        showEffectiveAmount(totalAmount.subtract(discount.getCouponAmount()), site);
    }

    private void showCouponInput(final BigDecimal totalAmount, final Site site) {
        show(totalAmount, site);
        configureDiscountCouponAmountDescription();
        configureViewsVisibilityDefault();
        mainContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (callback != null) {
                    callback.onInputRequestClicked();
                }
            }
        });
    }

    private void showNotAvailableDiscount(@NonNull final BigDecimal totalAmount,
        @NonNull final Site site) {
        configureViewsVisibilityWhenNotAvailableDiscount();
        amountDescription.setText(R.string.px_used_up_discount_row);
        amountDescription.setTextColor(getResources().getColor(R.color.px_form_text));
        showEffectiveAmount(totalAmount, site);
    }

    private void show(@NonNull final BigDecimal totalAmount, @NonNull final Site site) {
        configureViewsVisibilityDefault();
        amountDescription.setText(R.string.px_total_to_pay);
        amountDescription.setTextColor(getResources().getColor(R.color.px_form_text));
        showEffectiveAmount(totalAmount, site);
    }

    private void configureViewsVisibilityWhenNotAvailableDiscount() {
        amountBeforeDiscount.setVisibility(GONE);
        maxCouponAmount.setVisibility(GONE);
        arrow.setVisibility(VISIBLE);
        configureOnOnDetailClickedEvent();
    }

    private void configureViewsVisibilityDefault() {
        amountBeforeDiscount.setVisibility(GONE);
        maxCouponAmount.setVisibility(GONE);

        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) amountContainer.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
        }

        amountContainer.setLayoutParams(params);
        arrow.setVisibility(GONE);
    }

    private void configureDiscountCouponAmountDescription() {
        amountDescription.setVisibility(VISIBLE);
        amountDescription.setText(R.string.px_enter_coupon_code);
        amountDescription.setTextColor(getResources().getColor(R.color.px_discount_coupon));
    }

    private void showDiscount(@NonNull final Discount discount,
        @NonNull final Campaign campaign,
        @NonNull final BigDecimal totalAmount,
        @NonNull final Site site) {
        configureDiscountAmountDescription(discount, campaign);

        configureViewsVisibilityWhenDiscount(totalAmount, site);

        configureOnOnDetailClickedEvent();
    }

    private void configureOnOnDetailClickedEvent() {
        mainContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (callback != null) {
                    callback.onDetailClicked();
                }
            }
        });
    }

    private void configureViewsVisibilityWhenDiscount(@NonNull final BigDecimal totalAmount,
        @NonNull final Site site) {
        arrow.setVisibility(VISIBLE);
        amountBeforeDiscount.setVisibility(VISIBLE);
        TextFormatter.withCurrencyId(site.getCurrencyId())
            .withSpace()
            .amount(totalAmount)
            .normalDecimals()
            .into(amountBeforeDiscount);

        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) amountContainer.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.removeRule(RelativeLayout.ALIGN_PARENT_END);
        }

        amountContainer.setLayoutParams(params);
    }

    private void configureDiscountAmountDescription(final Discount discount, final Campaign campaign) {
        amountDescription.setVisibility(VISIBLE);
        amountDescription.setTextColor(getResources().getColor(R.color.px_discount_description));
        configureDiscountOffMessage(discount);
        configureMaxCouponAmountMessage(campaign);
    }

    private void showEffectiveAmount(@NonNull final BigDecimal totalAmount, @NonNull final Site site) {
        TextFormatter.withCurrencyId(site.getCurrencyId())
            .withSpace()
            .amount(totalAmount)
            .normalDecimals()
            .into(finalAmount);
    }

    private void configureMaxCouponAmountMessage(final Campaign campaign) {
        if (campaign.hasMaxCouponAmount()) {
            maxCouponAmount.setVisibility(VISIBLE);
            maxCouponAmount.setText(R.string.px_with_max_coupon_amount);
        } else {
            maxCouponAmount.setVisibility(GONE);
        }
    }

    private void configureDiscountOffMessage(final Discount discount) {
        if (discount.hasPercentOff()) {
            TextFormatter.withCurrencyId(discount.getCurrencyId())
                .noSpace().noSymbol()
                .amount(discount.getPercentOff())
                .normalDecimals()
                .into(amountDescription)
                .holder(R.string.px_discount_percent_off_percent);
        } else {
            TextFormatter.withCurrencyId(discount.getCurrencyId())
                .withSpace()
                .amount(discount.getAmountOff())
                .normalDecimals()
                .into(amountDescription)
                .holder(R.string.px_discount_amount_off);
        }
    }
}
