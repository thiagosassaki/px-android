package com.mercadopago.android.px.onetap;

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
import com.mercadopago.android.px.core.MercadoPagoComponents;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.onetap.components.OneTapContainer;
import com.mercadopago.android.px.tracker.Tracker;
import com.mercadopago.android.px.util.JsonUtil;
import com.mercadopago.android.px.viewmodel.CardPaymentModel;
import com.mercadopago.android.px.viewmodel.OneTapModel;
import java.math.BigDecimal;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class OneTapFragment extends Fragment implements OneTap.View {

    private static final String ARG_ONE_TAP_MODEL = "arg_onetap_model";
    private static final int REQ_CODE_CARD_VAULT = 0x999;

    //TODO move to CardValueActivity
    private static final String BUNDLE_TOKEN = "token";

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

        void onOneTapPay(@NonNull final PaymentMethod paymentMethod);

        void onOneTapPay(@NonNull final CardPaymentModel cardPaymentModel);

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
            presenter = new OneTapPresenter(model, session.getPluginRepository());
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
            //TODO change serializable output at least
            final String tokenString = data.getStringExtra(BUNDLE_TOKEN);
            final Token token = JsonUtil.getInstance().fromJson(tokenString, Token.class);
            presenter.onReceived(token);
        } else if (requestCode == REQ_CODE_CARD_VAULT && resultCode == RESULT_CANCELED && callback != null) {
            callback.onOneTapCardFlowCanceled();
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
    public void showPaymentFlow(@NonNull final PaymentMethod paymentMethod) {
        if (callback != null) {
            callback.onOneTapPay(paymentMethod);
        }
    }

    @Override
    public void showPaymentFlow(@NonNull final CardPaymentModel cardPaymentModel) {
        if (callback != null) {
            callback.onOneTapPay(cardPaymentModel);
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
    public void showCardFlow(@NonNull final OneTapModel model, @NonNull final Card card) {
        if (callback != null) {
            callback.onOneTapConfirmCardFlow();
        }
        new MercadoPagoComponents.Activities.CardVaultActivityBuilder()
            .setCard(card)
            .startActivity(this, REQ_CODE_CARD_VAULT);
    }
}
