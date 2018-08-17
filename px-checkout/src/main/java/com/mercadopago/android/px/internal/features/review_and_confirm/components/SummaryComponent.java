package com.mercadopago.android.px.internal.features.review_and_confirm.components;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.configuration.ReviewAndConfirmConfiguration;
import com.mercadopago.android.px.internal.features.review_and_confirm.SummaryProvider;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.SummaryModel;
import com.mercadopago.android.px.internal.view.Component;
import com.mercadopago.android.px.internal.view.RendererFactory;

public class SummaryComponent extends Component<SummaryComponent.SummaryProps, Void> {

    static {
        RendererFactory.register(SummaryComponent.class, SummaryRenderer.class);
    }

    private final SummaryProvider provider;

    static class SummaryProps {
        final SummaryModel summaryModel;
        final ReviewAndConfirmConfiguration reviewAndConfirmConfiguration;

        private SummaryProps(final SummaryModel summaryModel,
            final ReviewAndConfirmConfiguration reviewAndConfirmConfiguration) {
            this.summaryModel = summaryModel;
            this.reviewAndConfirmConfiguration = reviewAndConfirmConfiguration;
        }

        static SummaryProps createFrom(SummaryModel summaryModel,
            ReviewAndConfirmConfiguration reviewAndConfirmConfiguration) {
            return new SummaryProps(summaryModel, reviewAndConfirmConfiguration);
        }
    }

    SummaryComponent(@NonNull final SummaryComponent.SummaryProps props,
        @NonNull final SummaryProvider provider) {
        super(props);
        this.provider = provider;
    }

    FullSummary getFullSummary() {
        return new FullSummary(props, provider);
    }

    CompactSummary getCompactSummary() {
        return new CompactSummary(props.summaryModel);
    }
}
