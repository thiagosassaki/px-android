package com.mercadopago.android.px.internal.features.onetap.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.ResourceUtil;
import com.mercadopago.android.px.internal.util.textformatter.TextFormatter;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.PayerCost;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class MethodCard extends CompactComponent<MethodCard.Props, Void> {

    static class Props {

        /* default */ @NonNull final Card card;
        /* default */ @Nonnull final PayerCost payerCost;
        /* default */ @NonNull final String paymentMethodId;
        /* default */ @NonNull final String currencyId;
        /* default */ @Nullable final Discount discount;

        /* default */ Props(@NonNull final Card card,
            @Nonnull final PayerCost payerCost,
            @NonNull final String paymentMethodId,
            @NonNull final String currencyId,
            @Nullable final Discount discount) {
            this.card = card;
            this.payerCost = payerCost;
            this.paymentMethodId = paymentMethodId;
            this.currencyId = currencyId;
            this.discount = discount;
        }

        /* default */
        static Props createFrom(final PaymentMethod.Props props) {
            final Card card = props.getPaymentMethodSearch().getCardById(props.getCard().getId());
            final PayerCost payerCost = props.getCard().getAutoSelectedInstallment();

            return new Props(card, payerCost, props.paymentMethodId, props.currencyId, props.discount);
        }
    }

    /* default */ MethodCard(final Props props) {
        super(props);
    }

    @Override
    public View render(@Nonnull final ViewGroup parent) {
        final View main = inflate(parent, R.layout.px_payment_method_card_compact);
        final ImageView logo = main.findViewById(R.id.icon);
        final TextView name = main.findViewById(R.id.name);
        final String cardDescription =
            parent.getContext()
                .getString(R.string.px_card_hint, props.card.getIssuer().getName(), props.card.getLastFourDigits());
        name.setText(cardDescription);
        logo.setImageResource(ResourceUtil.getIconResource(parent.getContext(), props.paymentMethodId));
        resolveCft(main);
        resolveAmount(main);
        return main;
    }

    private void resolveAmount(final View main) {
        final ViewGroup row = main.findViewById(R.id.installments_row);
        if (props.payerCost.hasMultipleInstallments()) {
            row.setVisibility(View.VISIBLE);
            showAmount(row, props.payerCost);
        } else {
            row.setVisibility(View.GONE);
        }
    }

    private void showAmount(@NonNull final ViewGroup row, @NonNull final PayerCost autoSelectedInstallment) {

        final TextView totalAmountText = row.findViewById(R.id.total_amount);
        final TextView amountLessDiscountText = row.findViewById(R.id.amount_less_discount);

        TextFormatter
            .withCurrencyId(props.currencyId)
            .withSpace()
            .amount(autoSelectedInstallment.getTotalAmount())
            .add(props.discount)
            .normalDecimals()
            .into(totalAmountText)
            .strike()
            .visible(props.discount != null);

        TextFormatter.withCurrencyId(props.currencyId)
            .withSpace()
            .amount(autoSelectedInstallment.getTotalAmount())
            .normalDecimals()
            .into(amountLessDiscountText)
            .holder(R.string.px_total_amount_holder);
    }

    private void resolveCft(@NonNull final View main) {
        final TextView cft = main.findViewById(R.id.cft);
        final Context context = main.getContext();
        cft.setVisibility(props.payerCost.hasCFT() ? View.VISIBLE : View.GONE);
        cft.setText(props.payerCost.hasCFT() ? context
            .getString(R.string.px_installments_cft, props.payerCost.getCFTPercent()) : "");
    }
}
