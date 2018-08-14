package com.mercadopago.android.px.review_and_confirm.components;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.components.ActionDispatcher;
import com.mercadopago.android.px.components.Component;
import com.mercadopago.android.px.components.RendererFactory;
import com.mercadopago.android.px.review_and_confirm.SummaryProvider;
import com.mercadopago.android.px.review_and_confirm.models.ItemsModel;
import com.mercadopago.android.px.review_and_confirm.models.PaymentModel;
import com.mercadopago.android.px.preferences.ReviewAndConfirmPreferences;
import com.mercadopago.android.px.review_and_confirm.models.SummaryModel;
import com.mercadopago.android.px.review_and_confirm.models.TermsAndConditionsModel;

public class ReviewAndConfirmContainer extends Component<ReviewAndConfirmContainer.Props, Void> {

    private final SummaryProvider summaryProvider;

    static {
        RendererFactory.register(ReviewAndConfirmContainer.class, ReviewAndConfirmRenderer.class);
    }

    public ReviewAndConfirmContainer(@NonNull final Props props,
        @NonNull ActionDispatcher dispatcher,
        @NonNull final SummaryProvider summaryProvider) {
        super(props, dispatcher);
        this.summaryProvider = summaryProvider;
    }

    SummaryProvider getSummaryProvider() {
        return summaryProvider;
    }

    public boolean hasItemsEnabled() {
        return props.preferences.hasItemsEnabled();
    }

    public boolean hasDiscountTermsAndConditions() {
        return props.discountTermsAndConditionsModel != null;
    }

    public boolean hasMercadoPagoTermsAndConditions() {
        return props.mercadoPagoTermsAndConditionsModel != null;
    }

    public static class Props {
        /* default */ @Nullable final TermsAndConditionsModel mercadoPagoTermsAndConditionsModel;
        /* default */ @NonNull final PaymentModel paymentModel;
        /* default */ @NonNull final SummaryModel summaryModel;
        /* default */ @NonNull final ReviewAndConfirmPreferences preferences;
        /* default */ @NonNull final ItemsModel itemsModel;
        /* default */ @Nullable final TermsAndConditionsModel discountTermsAndConditionsModel;

        public Props(@Nullable final TermsAndConditionsModel mercadoPagoTermsAndConditionsModel,
            @NonNull final PaymentModel paymentModel,
            @NonNull final SummaryModel summaryModel,
            @NonNull final ReviewAndConfirmPreferences preferences,
            @NonNull final ItemsModel itemsModel,
            @Nullable final TermsAndConditionsModel discountTermsAndConditionsModel) {

            this.mercadoPagoTermsAndConditionsModel = mercadoPagoTermsAndConditionsModel;
            this.paymentModel = paymentModel;
            this.summaryModel = summaryModel;
            this.preferences = preferences;
            this.itemsModel = itemsModel;
            this.discountTermsAndConditionsModel = discountTermsAndConditionsModel;
        }
    }
}
