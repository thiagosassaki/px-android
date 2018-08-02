package com.mercadopago.android.px.plugins.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.components.Button;
import com.mercadopago.android.px.components.CompactComponent;
import com.mercadopago.android.px.components.Footer;
import com.mercadopago.android.px.components.HelpComponent;
import com.mercadopago.android.px.components.PaymentMethodComponent;
import com.mercadopago.android.px.components.Receipt;
import com.mercadopago.android.px.components.Renderer;
import com.mercadopago.android.px.components.RendererFactory;
import com.mercadopago.android.px.paymentresult.components.Header;
import com.mercadopago.android.px.paymentresult.props.HeaderProps;
import com.mercadopago.android.px.plugins.model.ExitAction;
import com.mercadopago.android.px.util.FragmentUtil;

public class BusinessPaymentRenderer extends Renderer<BusinessPaymentContainer> {

    @Override
    protected View render(@NonNull final BusinessPaymentContainer component,
        @NonNull final Context context,
        @Nullable final ViewGroup parent) {

        final LinearLayout mainContentContainer = CompactComponent.createLinearContainer(context);
        final LinearLayout headerContainer = CompactComponent.createLinearContainer(context);
        final ScrollView scrollView = CompactComponent.createScrollContainer(context);
        scrollView.addView(mainContentContainer);
        mainContentContainer.addView(headerContainer);

        final View header = renderHeader(component, headerContainer);

        final ViewTreeObserver vto = scrollView.getViewTreeObserver();

        if (component.props.payment.hasReceipt()) {
            RendererFactory.create(context, new Receipt(new Receipt.ReceiptProps(component.props.payment.getReceipt())))
                .render(headerContainer);
        }

        if (component.props.payment.hasHelp()) {
            final View helpView = new HelpComponent(component.props.payment.getHelp()).render(mainContentContainer);
            mainContentContainer.addView(helpView);
        }

        if (component.props.payment.hasTopFragment()) {
            FragmentUtil.addFragmentInside(mainContentContainer,
                R.id.px_fragmen_container_top,
                component.props.payment.getTopFragment());
        }

        if (component.props.payment.shouldShowPaymentMethod()) {
            renderPaymentMethod(component.props.paymentMethod, mainContentContainer);
        }

        if (component.props.payment.hasBottomFragment()) {
            FragmentUtil.addFragmentInside(mainContentContainer,
                R.id.px_fragmen_container_bottom,
                component.props.payment.getBottomFragment());
        }

        if (mainContentContainer.getChildCount() == 1) { //has only header
            vto.addOnGlobalLayoutListener(noBodyCorrection(mainContentContainer, scrollView, header));
        } else if (mainContentContainer.getChildCount() > 1) { // has more elements
            vto.addOnGlobalLayoutListener(
                bodyCorrection(mainContentContainer, scrollView, mainContentContainer.getChildAt(1)));
        }

        renderFooter(component, mainContentContainer);

        return scrollView;
    }

    private View renderPaymentMethod(@NonNull final PaymentMethodComponent.PaymentMethodProps props,
        @NonNull final LinearLayout mainContentContainer) {
        final PaymentMethodComponent paymentMethodComponent = new PaymentMethodComponent(props);
        RendererFactory.create(mainContentContainer.getContext(), paymentMethodComponent).render(mainContentContainer);
        return mainContentContainer.findViewById(R.id.mpsdkPaymentMethodContainer);
    }

    private ViewTreeObserver.OnGlobalLayoutListener bodyCorrection(final LinearLayout mainContentContainer,
        final ScrollView scrollView,
        final View toCorrect) {
        return new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final int diffHeight = calculateDiff(mainContentContainer, scrollView);
                if (diffHeight > 0) {
                    toCorrect.setPadding(toCorrect.getPaddingLeft(),
                        toCorrect.getPaddingTop() + (int) Math.ceil(diffHeight / 2f), toCorrect.getPaddingRight(),
                        toCorrect.getPaddingBottom() + (int) Math.ceil(diffHeight / 2f));
                }
            }
        };
    }

    private void renderFooter(@NonNull final BusinessPaymentContainer component,
        @NonNull final LinearLayout linearLayout) {
        final Button.Props primaryButtonProps = getButtonProps(component.props.payment.getPrimaryAction());
        final Button.Props secondaryButtonProps = getButtonProps(component.props.payment.getSecondaryAction());
        final Footer footer =
            new Footer(new Footer.Props(primaryButtonProps, secondaryButtonProps), component.getDispatcher());
        final View footerView = footer.render(linearLayout);
        linearLayout.addView(footerView);
    }

    @Nullable
    private Button.Props getButtonProps(final ExitAction action) {
        if (action != null) {
            final String label = action.getName();
            return new Button.Props(label, action);
        }
        return null;
    }

    @NonNull
    private View renderHeader(@NonNull final BusinessPaymentContainer component,
        @NonNull final LinearLayout linearLayout) {
        final Context context = linearLayout.getContext();
        final Header header = new Header(HeaderProps.from(component.props.payment, context), component.getDispatcher());
        final View render = RendererFactory.create(context, header).render(linearLayout);
        return render.findViewById(R.id.mpsdkPaymentResultContainerHeader);
    }

    @NonNull
    private ViewTreeObserver.OnGlobalLayoutListener noBodyCorrection(@NonNull final LinearLayout mainContentContainer,
        @NonNull final ScrollView scrollView,
        @NonNull final View header) {
        return new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final int diffHeight = calculateDiff(mainContentContainer, scrollView);
                if (diffHeight > 0) {
                    header
                        .setPadding(header.getPaddingLeft(), header.getPaddingTop() + (int) Math.ceil(diffHeight / 2f),
                            header.getPaddingRight(),
                            (header.getPaddingBottom() + (int) Math.ceil(diffHeight / 2f)));
                }
            }
        };
    }

    /* default */ int calculateDiff(final LinearLayout mainContentContainer, final ScrollView scrollView) {
        final int linearHeight = mainContentContainer.getMeasuredHeight();
        final int scrollHeight = scrollView.getMeasuredHeight();
        return scrollHeight - linearHeight;
    }
}
