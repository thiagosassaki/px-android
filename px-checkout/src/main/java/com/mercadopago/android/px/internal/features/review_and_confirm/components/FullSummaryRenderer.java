package com.mercadopago.android.px.internal.features.review_and_confirm.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.SummaryModel;
import com.mercadopago.android.px.internal.features.uicontrollers.payercosts.PayerCostColumn;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.internal.view.Renderer;
import com.mercadopago.android.px.internal.view.RendererFactory;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.internal.util.CurrenciesUtil;
import java.math.BigDecimal;

/**
 * Created by mromar on 2/28/18.
 */

public class FullSummaryRenderer extends Renderer<FullSummary> {

    @Override
    public View render(@NonNull final FullSummary component, @NonNull final Context context, final ViewGroup parent) {
        final View summaryView = inflate(R.layout.px_full_summary_component, parent);
        final MPTextView totalAmountTextView = summaryView.findViewById(R.id.mpsdkReviewSummaryTotalText);
        final FrameLayout payerCostContainer = summaryView.findViewById(R.id.mpsdkReviewSummaryPayerCostContainer);
        final MPTextView disclaimerTextView = summaryView.findViewById(R.id.mpsdkDisclaimer);
        final LinearLayout summaryDetailsContainer = summaryView.findViewById(R.id.mpsdkSummaryDetails);
        final LinearLayout reviewSummaryPayContainer = summaryView.findViewById(R.id.mpsdkReviewSummaryPay);
        final View firstSeparator = summaryView.findViewById(R.id.mpsdkFirstSeparator);

        final LinearLayout disclaimerLinearLayout = summaryView.findViewById(R.id.disclaimer);

        //summaryDetails list
        for (final AmountDescription amountDescription : component.getAmountDescriptionComponents()) {
            final Renderer amountDescriptionRenderer = RendererFactory.create(context, amountDescription);
            final View amountView = amountDescriptionRenderer.render();
            summaryDetailsContainer.addView(amountView);
        }

        if (shouldShowPayerCost(component.props.summaryModel)) {
            //payer cost
            final PayerCostColumn payerCostColumn =
                new PayerCostColumn(context, component.props.summaryModel.currencyId,
                    component.props.summaryModel.siteId, component.props.summaryModel.getInstallmentsRate(),
                    component.props.summaryModel.getInstallments(),
                    component.props.summaryModel.getPayerCostTotalAmount(),
                    component.props.summaryModel.getInstallmentAmount());
            payerCostColumn.inflateInParent(payerCostContainer, true);
            payerCostColumn.initializeControls();
            payerCostColumn.drawPayerCostWithoutTotal();
        } else {
            reviewSummaryPayContainer.setVisibility(View.GONE);
            firstSeparator.setVisibility(View.GONE);
        }

        //disclaimer
        if (shouldShowCftDisclaimer(component.props.summaryModel)) {
            final String disclaimer = getDisclaimer(component, context);
            final Renderer disclaimerRenderer =
                RendererFactory.create(context, component.getDisclaimerComponent(disclaimer));
            final View disclaimerView = disclaimerRenderer.render();
            disclaimerLinearLayout.addView(disclaimerView);
        }

        //total
        setText(totalAmountTextView,
            getFormattedAmount(component.props.summaryModel.getAmountToPay(), component.props.summaryModel.currencyId));

        //disclaimer
        setText(disclaimerTextView, component.getSummary().getDisclaimerText());
        disclaimerTextView.setTextColor(component.getSummary().getDisclaimerColor());

        return summaryView;
    }

    @VisibleForTesting
    boolean shouldShowCftDisclaimer(final SummaryModel props) {
        return PaymentTypes.isCreditCardPaymentType(props.getPaymentTypeId())
            && !TextUtil.isEmpty(props.getCftPercent());
    }

    @VisibleForTesting
    boolean shouldShowPayerCost(final SummaryModel props) {
        return PaymentTypes.isCreditCardPaymentType(props.getPaymentTypeId())
            && props.getInstallments() > 1;
    }

    @Nullable
    private Spanned getFormattedAmount(final BigDecimal amount, final String currencyId) {
        return amount != null && !TextUtil.isEmpty(currencyId) ? CurrenciesUtil
            .getSpannedAmountWithCurrencySymbol(amount, currencyId) : null;
    }

    public String getDisclaimer(FullSummary component, Context context) {
        return context.getString(R.string.px_installments_cft, component.props.summaryModel.getCftPercent());
    }
}
