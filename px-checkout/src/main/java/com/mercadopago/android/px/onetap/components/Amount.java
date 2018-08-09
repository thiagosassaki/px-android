package com.mercadopago.android.px.onetap.components;

import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.components.CompactComponent;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.CardPaymentMetadata;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.onetap.OneTap;
import com.mercadopago.android.px.viewmodel.OneTapModel;
import com.mercadopago.android.px.util.ViewUtils;
import com.mercadopago.android.px.util.textformatter.AmountFormatter;
import com.mercadopago.android.px.util.textformatter.CurrencyFormatter;
import com.mercadopago.android.px.util.textformatter.TextFormatter;
import java.math.BigDecimal;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class Amount extends CompactComponent<Amount.Props, OneTap.Actions> {

    /* default */ static class Props {
        /* default */ @NonNull final DiscountRepository discountRepository;
        /* default */ @Nullable final PayerCost payerCost;
        /* default */ final int installment;
        /* default */ @NonNull final PaymentSettingRepository config;

        /* default */ Props(@NonNull final DiscountRepository discountRepository,
            @NonNull final PaymentSettingRepository config,
            @Nullable final PayerCost payerCost,
            final int installment) {
            this.config = config;
            this.discountRepository = discountRepository;
            this.payerCost = payerCost;
            this.installment = installment;
        }

        /* default */
        static Props from(final OneTapModel props,
            final PaymentSettingRepository config,
            final DiscountRepository discountRepository) {
            final CardPaymentMetadata card = props.getPaymentMethods().getOneTapMetadata().getCard();
            final PayerCost payerCost = card != null ? card.getAutoSelectedInstallment() : null;
            return new Amount.Props(
                discountRepository,
                config,
                payerCost,
                payerCost == null ? PayerCost.NO_INSTALLMENTS : payerCost.getInstallments());
        }

        /* default */ boolean hasDiscount() {
            return discountRepository.getDiscount() != null && !discountRepository.isNotAvailableDiscount();
        }

        /* default */ boolean shouldShowPercentOff() {
            return hasDiscount() && discountRepository.getDiscount().hasPercentOff();
        }

        /* default */ boolean hasMultipleInstallments() {
            return installment > 1;
        }

        /* default */ boolean hasMaxDiscountLabel() {
            final Campaign campaign = discountRepository.getCampaign();
            return campaign != null && !BigDecimal.ZERO.equals(campaign.getMaxCouponAmount());
        }

        /* default */ String getCurrencyId() {
            return config.getCheckoutPreference().getSite().getCurrencyId();
        }

        public boolean isNotAvailableDiscount() {
            return discountRepository.isNotAvailableDiscount();
        }
    }

    /* default */ Amount(final Props props, final OneTap.Actions callBack) {
        super(props, callBack);
    }

    @Override
    public View render(@Nonnull final ViewGroup parent) {
        final View content = inflate(parent, R.layout.px_compact_amount);
        final ViewGroup discountLayout = content.findViewById(R.id.discount_detail_layout);
        resolveSmallAmountPlusDiscount(content);
        resolveBigAmount(content);
        resolveDiscount(discountLayout);
        resolveArrow(content);
        return content;
    }

    private void resolveDiscount(ViewGroup discountLayout) {
        if (props.isNotAvailableDiscount()) {
            resolveNotAvailableDiscount(discountLayout);
        } else {
            resolveOffAmount(discountLayout);
            resolveMaxDiscount(discountLayout);
        }
    }

    private void resolveNotAvailableDiscount(ViewGroup discountLayout) {
        final TextView discountMessage = discountLayout.findViewById(R.id.discount_message);
        final TextView discountMaxLabel = discountLayout.findViewById(R.id.discount_max_label);
        discountMessage.setVisibility(View.VISIBLE);
        discountLayout.setVisibility(View.VISIBLE);
        discountMaxLabel.setVisibility(View.GONE);

        discountMessage.setTextColor(discountLayout.getContext().getResources().getColor(R.color.px_color_payer_costs));
        discountMessage.setText(R.string.px_used_up_discount_row);
    }

    private void resolveArrow(@NonNull final View content) {
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                getActions().onAmountShowMore();
            }
        });
    }

    private void resolveOffAmount(@NonNull final ViewGroup discountLayout) {

        final TextView discountMessage = discountLayout.findViewById(R.id.discount_message);
        ViewUtils.showOrGone(discountLayout, props.hasDiscount());

        if (props.shouldShowPercentOff()) {
            TextFormatter.withCurrencyId(props.getCurrencyId())
                .noSpace().noSymbol()
                .amount(props.discountRepository.getDiscount().getPercentOff())
                .normalDecimals()
                .into(discountMessage)
                .holder(R.string.px_discount_percent_off_percent);
        } else if (props.hasDiscount()) {
            TextFormatter.withCurrencyId(props.getCurrencyId())
                .withSpace()
                .amount(props.discountRepository.getDiscount().getAmountOff())
                .normalDecimals()
                .into(discountMessage)
                .holder(R.string.px_discount_percent_off_amount);
        }

        ViewUtils.showOrGone(discountMessage, props.hasDiscount());
    }

    private void resolveMaxDiscount(@NonNull final ViewGroup discountLayout) {
        final TextView discountMaxLabel = discountLayout.findViewById(R.id.discount_max_label);
        ViewUtils.showOrGone(discountMaxLabel, props.hasDiscount() && props.hasMaxDiscountLabel());
    }

    private void resolveSmallAmountPlusDiscount(final View content) {
        final TextView amount = content.findViewById(R.id.amount);
        TextFormatter.withCurrencyId(props.getCurrencyId())
            .withSpace()
            .amount(props.config.getCheckoutPreference().getTotalAmount())
            .normalDecimals()
            .into(amount)
            .strike()
            .visible(props.hasDiscount() && !props.hasMultipleInstallments());
    }

    private void resolveBigAmount(final View content) {
        // amount with discount included.
        final TextView amountWithDiscount = content.findViewById(R.id.amount_with_discount);

        final CurrencyFormatter currencyFormatter = TextFormatter.withCurrencyId(props.getCurrencyId());

        final AmountFormatter amountFormatter = props.hasMultipleInstallments() ?
            currencyFormatter.amount(props.payerCost.getInstallmentAmount()) :
            currencyFormatter.amount(resolveAmountWithDiscount(props.config.getCheckoutPreference().getTotalAmount()));

        final CharSequence charSequence = amountFormatter
            .smallDecimals()
            .into(amountWithDiscount)
            .toSpannable();

        if (props.hasMultipleInstallments()) {
            final String x = amountWithDiscount.getContext().getString(R.string.px_installments_by);
            final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            spannableStringBuilder.append(props.payerCost.getInstallments().toString())
                .append(x)
                .append(" ")
                .append(charSequence);
            amountWithDiscount.setText(spannableStringBuilder);
        } else {
            amountWithDiscount.setText(charSequence);
        }
    }

    private BigDecimal resolveAmountWithDiscount(@NonNull final BigDecimal amount) {
        return !props.hasDiscount() ? amount
            : amount.subtract(props.discountRepository.getDiscount().getCouponAmount());
    }
}
