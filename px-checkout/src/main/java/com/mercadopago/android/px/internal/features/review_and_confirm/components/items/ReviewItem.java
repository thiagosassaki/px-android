package com.mercadopago.android.px.internal.features.review_and_confirm.components.items;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.ItemModel;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.Component;
import com.mercadopago.android.px.internal.view.RendererFactory;

public class ReviewItem extends Component<ReviewItem.Props, Void> {

    static {
        RendererFactory.register(ReviewItem.class, ReviewItemRenderer.class);
    }

    public ReviewItem(@NonNull final Props props) {
        super(props);
    }

    public ReviewItem(@NonNull final Props props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public boolean hasItemImage() {
        return TextUtil.isNotEmpty(props.itemModel.imageUrl);
    }

    public boolean hasIcon() {
        return props.icon != null;
    }

    public static class Props {

        final ItemModel itemModel;
        @DrawableRes final
        Integer icon;
        final String quantityLabel;
        final String unitPriceLabel;

        public Props(final ItemModel itemModel,
            @DrawableRes @Nullable final Integer icon,
            final String quantityLabel,
            final String unitPriceLabel) {
            this.itemModel = itemModel;
            this.icon = icon;
            this.quantityLabel = quantityLabel;
            this.unitPriceLabel = unitPriceLabel;
        }
    }
}
