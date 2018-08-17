package com.mercadopago.android.px.internal.features.onetap.components;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.internal.view.DiscountDetailContainer;
import com.mercadopago.android.px.internal.view.DiscountDetailContainer.Props.DialogTitleType;
import com.mercadopago.android.px.model.Item;
import java.util.List;
import javax.annotation.Nonnull;

public class PaymentDetailContainer extends CompactComponent<PaymentDetailContainer.Props, Void> {

    public static final class Props {
        /* default */ final DiscountRepository discountRepository;
        /* default */ final List<Item> items;
        /* default */ final String currencyId;

        public Props(final DiscountRepository discountRepository, final List<Item> items, final String currencyId) {
            this.discountRepository = discountRepository;
            this.items = items;
            this.currencyId = currencyId;
        }
    }

    public PaymentDetailContainer(@NonNull final PaymentDetailContainer.Props props) {
        super(props);
    }

    @Override
    public View render(@Nonnull final ViewGroup parent) {
        addItemDetails(parent);
        addDiscount(parent);
        return null;
    }

    private void addItemDetails(@NonNull final ViewGroup parent) {
        for (final Item item : props.items) {
            parent.addView(new DetailItem(new DetailItem.Props(item, props.currencyId)).render(parent));
        }
    }

    private void addDiscount(@NonNull final ViewGroup parent) {
        if (props.discountRepository.hasValidDiscount() || props.discountRepository.isNotAvailableDiscount()) {
            final DiscountDetailContainer discountDetailContainer = new DiscountDetailContainer(
                new DiscountDetailContainer.Props(DialogTitleType.SMALL, props.discountRepository));
            discountDetailContainer.render(parent);
        }
    }
}