package com.mercadopago.android.px.internal.features.review_and_confirm.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.SummaryModel;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.internal.view.Renderer;
import com.mercadopago.android.px.internal.view.RendererFactory;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.internal.util.CurrenciesUtil;

public class CompactSummaryRenderer extends Renderer<CompactSummary> {

    @Override
    public View render(@NonNull final CompactSummary component, @NonNull final Context context,
        final ViewGroup parent) {
        final View summaryView = inflate(R.layout.px_compact_summary_component, parent);
        final MPTextView totalAmountTextView = summaryView.findViewById(R.id.mpsdkTotalAmount);
        final MPTextView itemTitleTextView = summaryView.findViewById(R.id.mpsdkItemTitle);
        final LinearLayout disclaimerLinearLayout = summaryView.findViewById(R.id.disclaimer);

        setText(totalAmountTextView, CurrenciesUtil
            .getSpannedAmountWithCurrencySymbol(component.props.getAmountToPay(), component.props.currencyId));
        setText(itemTitleTextView, getItemTitle(component.props.title, context));

        if (shouldShowCftDisclaimer(component.props)) {
            final String disclaimer = getDisclaimer(component, context);
            final Renderer disclaimerRenderer =
                RendererFactory.create(context, component.getDisclaimerComponent(disclaimer));
            final View disclaimerView = disclaimerRenderer.render();
            disclaimerLinearLayout.addView(disclaimerView);
        }

        return summaryView;
    }

    @VisibleForTesting
    boolean shouldShowCftDisclaimer(final SummaryModel props) {
        return PaymentTypes.isCreditCardPaymentType(props.getPaymentTypeId())
            && !TextUtil.isEmpty(props.getCftPercent());
    }

    private String getItemTitle(String itemTitle, Context context) {
        return TextUtil.isEmpty(itemTitle) ? getDefaultTitle(context) : itemTitle;
    }

    private String getDefaultTitle(Context context) {
        return context.getString(R.string.px_review_summary_product);
    }

    private String getDisclaimer(CompactSummary component, Context context) {
        return context.getString(R.string.px_installments_cft, component.props.getCftPercent());
    }
}
