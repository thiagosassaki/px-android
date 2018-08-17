package com.mercadopago.android.px.internal.features.onetap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.CheckoutActivity;
import com.mercadopago.android.px.internal.features.MercadoPagoComponents;
import com.mercadopago.android.px.internal.features.onetap.components.OneTapContainer;
import com.mercadopago.android.px.internal.features.plugins.PaymentProcessorPluginActivity;
import com.mercadopago.android.px.internal.tracker.Tracker;
import com.mercadopago.android.px.internal.util.ErrorUtil;
import com.mercadopago.android.px.internal.viewmodel.OneTapModel;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.math.BigDecimal;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class OneTapFragment extends Fragment implements OneTap.View {

    private static final String ARG_ONE_TAP_MODEL = "arg_onetap_model";
    private static final int REQ_CODE_CARD_VAULT = 0x999;
    private static final int REQ_CODE_PAYMENT_PROCESSOR = 0x123;

    private CallBack callback;
    OneTapPresenter presenter;

    //TODO remove - just for tracking
    private BigDecimal amountToPay;
    private boolean hasDiscount;

    public static OneTapFragment getInstance(@NonNull final OneTapModel oneTapModel) {
        final OneTapFragment oneTapFragment = new OneTapFragment();
        final Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_ONE_TAP_MODEL, oneTapModel);
        oneTapFragment.setArguments(bundle);
        return oneTapFragment;
    }

    public interface CallBack {

        void onOneTapCanceled();

        void onChangePaymentMethod();

        void onOneTapConfirmCardFlow();

        void onOneTapCardFlowCanceled();
    }

    @Override
    public void onResume() {
        super.onResume();
        final OneTapModel model = (OneTapModel) getArguments().getSerializable(ARG_ONE_TAP_MODEL);
        configureView(getView(), presenter, model);
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof CallBack) {
            callback = (CallBack) context;
        }
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.px_onetap_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        final Bundle arguments = getArguments();
        if (arguments != null) {
            final Session session = Session.getSession(view.getContext());
            amountToPay = session.getAmountRepository().getAmountToPay();
            hasDiscount = session.getDiscountRepository().getDiscount() != null;
            final OneTapModel model = (OneTapModel) arguments.getSerializable(ARG_ONE_TAP_MODEL);
            presenter = new OneTapPresenter(model,
                session.getPaymentRepository());
            configureView(view, presenter, model);
            presenter.attachView(this);
            trackScreen(model);
        }
    }

    private void trackScreen(final OneTapModel model) {
        if (getActivity() != null) {
            Tracker.trackOneTapScreen(getActivity().getApplicationContext(), model.getPublicKey(),
                    model.getPaymentMethods().getOneTapMetadata(), amountToPay);
        }
    }

    @Override
    public void cancel() {
        if (callback != null) {
            callback.onOneTapCanceled();
        }
    }

    private void configureView(final View view, final OneTap.Actions actions, final OneTapModel model) {
        final ViewGroup container = view.findViewById(R.id.main_container);
        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        container.removeAllViews();
        configureToolbar(toolbar);
        new OneTapContainer(model, actions).render(container);
    }

    private void configureToolbar(final Toolbar toolbar) {
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && toolbar != null) {
            activity.setSupportActionBar(toolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    presenter.cancel();
                }
            });
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == REQ_CODE_CARD_VAULT && resultCode == RESULT_OK) {
            presenter.onTokenResolved();
        } else if (requestCode == REQ_CODE_CARD_VAULT && resultCode == RESULT_CANCELED && callback != null) {
            callback.onOneTapCardFlowCanceled();
        } else if (requestCode == REQ_CODE_PAYMENT_PROCESSOR && getActivity() != null) {
            ((CheckoutActivity) getActivity()).resolvePaymentProcessor(resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void changePaymentMethod() {
        if (callback != null) {
            callback.onChangePaymentMethod();
        }
    }

    @Override
    public void showDetailModal(@NonNull final OneTapModel model) {
        PaymentDetailInfoDialog.showDialog(getChildFragmentManager());
    }

    @Override
    public void trackConfirm(final OneTapModel model) {
        if (getActivity() != null) {
            Tracker.trackOneTapConfirm(getActivity().getApplicationContext(), model.getPublicKey(),
                    model.getPaymentMethods().getOneTapMetadata(), amountToPay);
        }
    }

    @Override
    public void trackCancel(final String publicKey) {
        if (getActivity() != null) {
            Tracker.trackOneTapCancel(getActivity().getApplicationContext(), publicKey);
        }
    }

    @Override
    public void trackModal(final OneTapModel model) {
        if (getActivity() != null) {
            Tracker
                    .trackOneTapSummaryDetail(getActivity().getApplicationContext(), model.getPublicKey(), hasDiscount,
                            model.getPaymentMethods().getOneTapMetadata().getCard());
        }
    }

    @Override
    public void showPaymentProcessor() {
        PaymentProcessorPluginActivity.start(this, REQ_CODE_PAYMENT_PROCESSOR);
    }

    @Override
    public void showErrorView(@NonNull final MercadoPagoError error) {
        if (getActivity() != null) {
            ErrorUtil.startErrorActivity(getActivity(), error);
        }
    }

    @Override
    public void showBusinessResult(final BusinessPayment businessPayment) {
        //TODO refactor
        if (getActivity() != null) {
            ((CheckoutActivity) getActivity()).presenter.onBusinessResult(businessPayment);
        }
    }

    @Override
    public void showPaymentResult(final PaymentResult paymentResult) {
        //TODO refactor
        if (getActivity() != null) {
            ((CheckoutActivity) getActivity()).presenter.checkStartPaymentResultActivity(paymentResult);
        }
    }

    @Override
    public void showCardFlow(@NonNull final OneTapModel model, @NonNull final Card card) {
        if (callback != null) {
            callback.onOneTapConfirmCardFlow();
        }
        new MercadoPagoComponents.Activities.CardVaultActivityBuilder()
            .setCard(card)
            .startActivity(this, REQ_CODE_CARD_VAULT);
    }
}
